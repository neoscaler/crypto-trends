package com.neoscaler.cryptotrends.application.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
// Explicitly no ForeignKey Annotation because this would trigger if reinserting the currencies with ON CONFLICT REPACE
public class CurrencyUserData {

  @PrimaryKey
  @NonNull
  private String id;

  private boolean favorite;

  private boolean watched;

}
