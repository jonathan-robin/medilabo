package com.service.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "genre")
    private String genre;

    @Column(name = "patient_address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;
}