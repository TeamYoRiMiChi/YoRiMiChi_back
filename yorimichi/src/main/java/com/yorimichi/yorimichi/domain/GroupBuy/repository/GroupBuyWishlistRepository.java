package com.yorimichi.yorimichi.domain.GroupBuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yorimichi.yorimichi.domain.GroupBuy.entity.GroupBuyWishlist;


public interface GroupBuyWishlistRepository 
	extends JpaRepository<GroupBuyWishlist, Long>{
		
		
		//이미찜했는지확인
		boolean existsByMemberIdAndProductId(
				Long memberId,
				Long productId
				
				);
		
		
		//찜한거삭제하기
		void deleteByMemberIdAndProductId(
				Long memberId,
				Long productId
				
				
				);
		
		
		
		
		
		
	}

