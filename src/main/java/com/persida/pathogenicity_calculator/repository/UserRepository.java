package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.repository.jpa.BasicUserDataJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value="SELECT * FROM `pc_user` AS U WHERE U.username = :username ;", nativeQuery = true)
  public User getUserByUsername(@Param("username") String username);

  @Query(value="SELECT * FROM `pc_user` AS U WHERE U.user_id  = :userid ;", nativeQuery = true)
  public User getUserById(@Param("userid") int userid);

  @Query(value="SELECT U.`user_id`, U.`username`, U.`email`, U.`enabled` FROM `pc_user` AS U WHERE username = :username ;", nativeQuery = true)
  public BasicUserDataJPA getBacisUserDataByUsername(@Param("username") String username);
}
