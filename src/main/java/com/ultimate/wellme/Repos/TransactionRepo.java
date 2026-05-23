package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ultimate.wellme.models.Transaction;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    
}
