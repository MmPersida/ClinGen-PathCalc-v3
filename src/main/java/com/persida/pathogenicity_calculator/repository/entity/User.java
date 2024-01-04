package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = AbstractEntity.TABLE_PREFIX + "user")
public class User extends AbstractEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String username;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String password;

  private String role;

  private Boolean enabled;

  private String email;

  @OneToMany(mappedBy = "user")
  protected Set<VariantInterpretation> VariantInterpretations;

  public User(String username, String firstName, String lastName, String role){
    super();
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
    this.password = "1234";
    this.enabled = true;
  }
}