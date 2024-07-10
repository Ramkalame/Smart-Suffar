package com.rido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.CourierDocument;

@Repository
public interface CourierDocumentRepository extends JpaRepository<CourierDocument, Long> {


}
