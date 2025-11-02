# Trách nhiệm xác định Event thành công trong Outbox Pattern

## 📋 Trả lời ngắn gọn

**`OutboxEventProcessor`** là component **chịu trách nhiệm chính** để:
1. ✅ **Quyết định** event đã publish thành công hay thất bại
2. ✅ **Cập nhật status** trong database (PENDING → SENT/FAILED/DLQ)
3. ✅ **Quản lý retry logic** (số lần thử, max attempts)

---

## 🏗️ Phân chia trách nhiệm (Responsibility)

### 1. **OutboxEventServiceImpl** - Lưu Event ban đầu

**Trách nhiệm:**
- Lưu event vào database với status = `PENDING`
- Chỉ đảm bảo event được lưu trong transaction
- **KHÔNG** quyết định thành công hay thất bại

```java
// OutboxEventServiceImpl
@Transactional
public OutboxEvent saveOutboxEvent(...) {
    OutboxEvent event = OutboxEvent.builder()
            .status(OutboxStatus.PENDING)  // Luôn là PENDING khi tạo
            .attempts(0)
            ...
            .build();
    
    return outboxEventRepository.save(event);
    // ✅ Event được lưu với status = PENDING
}
```

### 2. **RedisStreamPublisherImpl** - Publish Event

**Trách nhiệm:**
- Publish event vào Redis Stream
- Trả về **boolean**:
  - `true` = publish thành công (Redis đã nhận message)
  - `false` = publish thất bại (lỗi khi publish)
- **KHÔNG** update status trong database

```java
// RedisStreamPublisherImpl
public boolean publishToStream(OutboxEvent event) {
    try {
        // Publish vào Redis Stream
        RecordId recordId = redisTemplate.opsForStream().add(record);
        
        // ✅ Thành công nếu recordId != null
        return recordId != null;
    } catch (Exception e) {
        log.error("Failed to publish", e);
        // ❌ Thất bại nếu có exception
        return false;
    }
}
```

### 3. **OutboxEventProcessor** - Quyết định Thành công/Thất bại ⭐

**Trách nhiệm chính:**
1. Đọc PENDING events từ database
2. Gọi `RedisStreamPublisher` để publish
3. **Quyết định** thành công hay thất bại dựa trên kết quả
4. **Cập nhật status** trong database:
   - `PENDING` → `SENT` (nếu thành công)
   - `PENDING` → `FAILED` (nếu thất bại, attempts < 3)
   - `PENDING` → `DLQ` (nếu thất bại, attempts >= 3)
5. **Quản lý retry** (tăng attempts, check max attempts)

```java
// OutboxEventProcessor
@Scheduled(fixedDelay = 5000)
@Transactional
public void processPendingEvents() {
    // 1. Đọc PENDING events
    List<OutboxEvent> pendingEvents = 
        outboxEventRepository.findPendingEvents(OutboxStatus.PENDING);
    
    for (OutboxEvent event : pendingEvents) {
        try {
            // 2. Publish vào Redis Stream
            boolean success = redisStreamPublisher.publishToStream(event);
            
            // 3. ⭐ QUYẾT ĐỊNH THÀNH CÔNG/THẤT BẠI
            if (success) {
                // ✅ THÀNH CÔNG
                event.setStatus(OutboxStatus.SENT);
                processed++;
                log.debug("Successfully processed: id={}", event.getId());
            } else {
                // ❌ THẤT BẠI
                event.setAttempts(event.getAttempts() + 1);
                failed++;
                
                if (event.getAttempts() >= MAX_ATTEMPTS) {
                    // Quá số lần thử → DLQ
                    event.setStatus(OutboxStatus.DLQ);
                    log.error("Moved to DLQ: id={}", event.getId());
                } else {
                    // Còn cơ hội retry → FAILED
                    event.setStatus(OutboxStatus.FAILED);
                }
            }
            
            // 4. ⭐ CẬP NHẬT STATUS trong database
            outboxEventRepository.save(event);
            
        } catch (Exception e) {
            // Exception → Thất bại
            event.setAttempts(event.getAttempts() + 1);
            event.setStatus(event.getAttempts() >= MAX_ATTEMPTS 
                ? OutboxStatus.DLQ 
                : OutboxStatus.FAILED);
            outboxEventRepository.save(event);
            failed++;
        }
    }
}
```

---

## 🔄 Flow xác định Thành công

```
┌─────────────────────────────────────────────────────────┐
│ 1. OutboxEventServiceImpl.saveOutboxEvent()            │
│    - Lưu event vào DB                                    │
│    - Status: PENDING                                     │
│    - Attempts: 0                                         │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ Event saved with PENDING
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│ 2. OutboxEventProcessor.processPendingEvents()          │
│    (Chạy mỗi 5 giây)                                     │
│                                                          │
│    a) Query: SELECT * FROM outbox_events                │
│       WHERE status = 'PENDING'                           │
│                                                          │
│    b) For each PENDING event:                           │
│       ┌─────────────────────────────────────────────┐   │
│       │ 3. redisStreamPublisher.publishToStream()  │   │
│       │    - Publish vào Redis Stream                │   │
│       │    - Return: boolean (true/false)           │   │
│       └──────────────┬──────────────────────────────┘   │
│                      │                                    │
│       ┌──────────────▼──────────────────────────────┐   │
│       │ 4. ⭐ QUYẾT ĐỊNH THÀNH CÔNG/THẤT BẠI       │   │
│       │                                             │   │
│       │  if (success == true) {                    │   │
│       │      ✅ Status = SENT                      │   │
│       │  } else {                                  │   │
│       │      ❌ Status = FAILED or DLQ            │   │
│       │      attempts++                            │   │
│       │  }                                         │   │
│       └──────────────┬──────────────────────────────┘   │
│                      │                                    │
│       ┌──────────────▼──────────────────────────────┐   │
│       │ 5. ⭐ CẬP NHẬT STATUS trong DB             │   │
│       │    outboxEventRepository.save(event)        │   │
│       └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Tiêu chí xác định Thành công

### Thành công (Success = true)
- Redis Stream `add()` trả về `RecordId` (không null)
- Message đã được Redis accept và lưu trong stream
- Status → `SENT`

### Thất bại (Success = false)
- Redis Stream `add()` trả về `null`
- Exception xảy ra khi publish
- Status → `FAILED` (attempts < 3) hoặc `DLQ` (attempts >= 3)

---

## 📊 Ví dụ cụ thể

### Scenario 1: Thành công

```java
// 1. Event được lưu
OutboxEvent event = saveOutboxEvent("JOB", 456, "CREATED", payload);
// Status: PENDING, Attempts: 0

// 2. Processor chạy (sau 5 giây)
boolean success = redisStreamPublisher.publishToStream(event);
// Redis Stream.add() trả về RecordId → success = true

// 3. Processor quyết định
if (success) {  // true
    event.setStatus(OutboxStatus.SENT);  // ✅ PENDING → SENT
    outboxEventRepository.save(event);
}

// Kết quả:
// Status: SENT
// Attempts: 0
```

### Scenario 2: Thất bại lần 1 (sẽ retry)

```java
// 1. Event được lưu
OutboxEvent event = saveOutboxEvent("JOB", 456, "CREATED", payload);
// Status: PENDING, Attempts: 0

// 2. Processor chạy (sau 5 giây)
boolean success = redisStreamPublisher.publishToStream(event);
// Redis connection failed → success = false

// 3. Processor quyết định
if (!success) {  // false
    event.setAttempts(event.getAttempts() + 1);  // 0 → 1
    if (event.getAttempts() < 3) {
        event.setStatus(OutboxStatus.FAILED);  // ❌ PENDING → FAILED
    }
    outboxEventRepository.save(event);
}

// Kết quả:
// Status: FAILED
// Attempts: 1
// → Processor sẽ retry trong lần chạy tiếp theo
```

### Scenario 3: Thất bại 3 lần (vào DLQ)

```java
// 1. Event đã retry 2 lần
// Status: FAILED, Attempts: 2

// 2. Processor chạy lần 3
boolean success = redisStreamPublisher.publishToStream(event);
// Vẫn thất bại → success = false

// 3. Processor quyết định
if (!success) {  // false
    event.setAttempts(event.getAttempts() + 1);  // 2 → 3
    if (event.getAttempts() >= 3) {
        event.setStatus(OutboxStatus.DLQ);  // ❌ FAILED → DLQ
    }
    outboxEventRepository.save(event);
}

// Kết quả:
// Status: DLQ (Dead Letter Queue)
// Attempts: 3
// → Cần xử lý manually hoặc alert
```

---

## 🎯 Tóm tắt

| Component | Trách nhiệm | Quyết định Thành công? |
|-----------|-------------|------------------------|
| **OutboxEventServiceImpl** | Lưu event ban đầu | ❌ Không - chỉ lưu với PENDING |
| **RedisStreamPublisherImpl** | Publish vào Redis Stream | ⚠️ Một phần - trả về boolean |
| **OutboxEventProcessor** ⭐ | Xử lý và quyết định | ✅ **CÓ** - quyết định dựa trên kết quả publish và cập nhật status |

---

## 🔑 Điểm quan trọng

1. **OutboxEventProcessor** là component **DUY NHẤT** cập nhật status từ `PENDING` → `SENT`/`FAILED`/`DLQ`

2. **RedisStreamPublisherImpl** chỉ trả về boolean, **KHÔNG** cập nhật database

3. **Thành công** được xác định bởi:
   - Redis Stream `add()` trả về `RecordId != null`
   - Không có exception

4. **Retry logic** được quản lý bởi `OutboxEventProcessor`:
   - Tăng `attempts` mỗi lần thất bại
   - Chuyển sang `DLQ` sau 3 lần thử

---

## 📝 Kết luận

**`OutboxEventProcessor`** là component **CHỊU TRÁCH NHIỆM CHÍNH** để:
- ✅ Quyết định event thành công hay thất bại
- ✅ Cập nhật status trong database
- ✅ Quản lý retry logic

Đây là component quan trọng nhất trong việc đảm bảo **reliable event publishing**.

