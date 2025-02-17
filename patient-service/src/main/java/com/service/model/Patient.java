package com.service.model;

import java.time.LocalDate;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message= "Le nom est obligatoire")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    @NotBlank(message="Le prénom est obligatoire")
    private String firstName;

    @Column(name = "birth_date")
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthDate;

    @Column(name = "gender")
    @NotBlank(message = "Le genre est obligatoire")
    @Pattern(regexp = "M|F", message = "Le genre doit être 'M' ou 'F'")
    private String gender;

    @Column(name = "address")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Le numéro de téléphone est invalide")
    private String phoneNumber;
}