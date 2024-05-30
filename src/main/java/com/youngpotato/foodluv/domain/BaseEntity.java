package com.youngpotato.foodluv.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "cre_time", nullable = false)
    protected LocalDateTime creTime;

    @CreatedBy
    @Column(name = "cre_member_id", nullable = false)
    protected Long creMemberId;

    @LastModifiedDate
    @Column(name = "upd_time", nullable = false)
    protected LocalDateTime updTime;

    @LastModifiedBy
    @Column(name = "upd_member_id", nullable = false)
    protected Long updMemberId;
}
