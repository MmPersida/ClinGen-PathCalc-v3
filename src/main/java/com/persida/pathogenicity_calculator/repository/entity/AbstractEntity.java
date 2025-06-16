package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {

  public static final String TABLE_PREFIX = "pc_";

  public AbstractEntity() {}

  @PrePersist
  public void onPrePersist() {
    this.createdOn = new Date();
    this.createdBy = this.modifiedBy == null ? EntityModifier.PC_BACKEND.name() : this.createdBy;
    this.modifiedOn = new Date();
    this.modifiedBy = this.modifiedBy == null ? EntityModifier.PC_BACKEND.name() : this.modifiedBy;
  }

  @PreUpdate
  public void onPreUpdate() {
    this.modifiedOn = new Date();
    this.modifiedBy = this.modifiedBy == null ? EntityModifier.PC_BACKEND.name() : this.modifiedBy;
  }

  @Column(name = "created_on")
  private Date createdOn;

  @Column(name = "modified_on")
  private Date modifiedOn;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "modified_by")
  private String modifiedBy;
}
