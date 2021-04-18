package dao;

import exceptions.DaoException;
import model.Rate;

import java.util.List;

public interface RateDao {

  /**
   * create a rate
   * @param rate rate of a seller given by a user
   * @return created rate
   * @throws DaoException
   */
  Rate create(Rate rate) throws DaoException;

  /**
   * create or update a rate
   * @param raterId rater's id
   * @param sellerId seller's id
   * @param rate rate
   * @return created or updated rate
   * @throws DaoException
   */
  Rate createOrUpdate(String raterId, String sellerId, Rate rate) throws DaoException;

  /**
   * read a rate given rater's id and seller's id
   * @param raterId rater's id
   * @param sellerId seller's id
   * @return rate of the rater given to the sellers
   * @throws DaoException
   */
  Rate read(String raterId, String sellerId) throws DaoException;

  /**
   * read a list of rates of a seller
   * @param sellerId the seller's id
   * @return a list of rates of that seller
   * @throws DaoException
   */
  List<Rate> read(String sellerId) throws DaoException;

  Double readAvgRateOfASeller(String sellerId) throws DaoException;

  /**
   * update a rate
   * @param raterId rater's id
   * @param sellerId seller's id
   * @param rate rate
   * @return updated rate
   * @throws DaoException
   */
  Rate update(String raterId, String sellerId, Rate rate) throws DaoException;

  /**
   * delete a rate
   * @param raterId rater's id
   * @param sellerId seller's id
   * @return deleted rate
   * @throws DaoException
   */
  Rate delete(String raterId, String sellerId) throws DaoException;
}
