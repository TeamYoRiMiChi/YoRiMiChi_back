package com.yorimichi.yorimichi.domain.GroupBuy.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "whishlist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyWishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    public GroupBuyWishlist(Long memberId, Long productId) {
        this.memberId = memberId;
        this.productId = productId;
    }

	
}
