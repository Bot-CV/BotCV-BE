package org.toanehihi.botcv.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "locations")
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(name = "street_address")
    private String streetAddress;    

    private String ward;            

    private String district;        

    @Column(name = "province_city")
    private String provinceCity;     

    private String country;         

	@Column(name = "lat")
	private BigDecimal lat;

	@Column(name = "lng")
	private BigDecimal lng;

	@CreationTimestamp
	@Column(name = "date_created", nullable = false, updatable = false)
	private OffsetDateTime dateCreated;

	@UpdateTimestamp
	@Column(name = "date_updated", nullable = false)
	private OffsetDateTime dateUpdated;

}


