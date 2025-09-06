package com.seasontone.repository.user;

import com.seasontone.Entity.EmailCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailCodeRepository extends CrudRepository<EmailCode, String> {
}
