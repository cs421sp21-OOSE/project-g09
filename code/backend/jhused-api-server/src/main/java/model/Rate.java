package model;

import lombok.Data;

@Data
public class Rate {
  private String raterId;
  private String sellerId;
  private int rate;

  public Rate(){
  }

  public Rate(String raterId, String sellerId, int rate){
    this.raterId = raterId;
    this.sellerId = sellerId;
    this.rate = rate;
  }
}
