package services

import javax.inject.Singleton

import com.google.inject.Inject
import controllers.{Loan, LoanId, LoanRequest, LoanUUId, Offer, OfferId, OfferRequest, OfferUUId}

import scala.collection.mutable

trait LoanRepository {
  def storeLoan(loanRequest: LoanRequest): Loan
  def getLoan(loanId: LoanId): Option[Loan]

  def storeOffer(loanId: LoanId, offer: OfferRequest): Offer
  def getOffers(loanId: LoanId): List[Offer]
}

@Singleton
class InMemoryLoanRepository @Inject() (idGenerator: IdGenerator) extends LoanRepository {

  val loanStore = mutable.Map[LoanUUId, Loan]()
  val offerStore = mutable.Map[LoanUUId, mutable.Map[OfferUUId, Offer]]()

  override def storeLoan(loanRequest: LoanRequest): Loan = {
    val loanId = LoanId(idGenerator.generateId())
    val loan = Loan(loanId, loanRequest)
    loanStore.put(loanId.loanId, loan)
    offerStore.put(loanId.loanId, mutable.Map.empty)
    loan
  }

  override def getLoan(loanId: LoanId): Option[Loan] =
    loanStore.get(loanId.loanId)

  def storeOffer(loanId: LoanId, offerRequest: OfferRequest): Offer = {
    val offerId = OfferId(idGenerator.generateId())
    val offer = Offer(offerId, loanId, offerRequest)
    val offerMap = offerStore.getOrElse(loanId.loanId, mutable.Map.empty)
    offerMap.put(offerId.offerId, offer)
    offerStore.put(loanId.loanId, offerMap)
    offer
  }

  override def getOffers(loanId: LoanId): List[Offer] =
    offerStore.get(loanId.loanId).map(m => m.values.toList).getOrElse(List())

}


