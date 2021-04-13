package model;

import lombok.Data;

/**
 * Model Rate
 */
@Data
public class Rate {
  private String raterId;
  private String sellerId;
  private int rate;

  /**
   * Empty constructor used by resultSetLinkedHashMapAccumulatorProvider
   */
  public Rate(){
  }

  /**
   * Full constructor
   * @param raterId the JHED of the user who rated the seller user
   * @param sellerId the JHED of the seller user
   * @param rate the rate, int, ranged from [0,6)
   */
  public Rate(String raterId, String sellerId, int rate){
    this.raterId = raterId;
    this.sellerId = sellerId;
    this.rate = rate;
  }
}
