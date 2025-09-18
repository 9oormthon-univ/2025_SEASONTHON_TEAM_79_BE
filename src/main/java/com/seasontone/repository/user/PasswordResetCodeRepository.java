package com.seasontone.repository.user;

import com.seasontone.domain.users.PasswordResetCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetCodeRepository extends CrudRepository<PasswordResetCode, String> {

}

