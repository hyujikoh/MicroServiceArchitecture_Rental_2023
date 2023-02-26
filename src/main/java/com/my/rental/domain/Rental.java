package com.my.rental.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.my.rental.domain.enumeration.RentalStatus;

/**
 * A Rental.
 */
@Entity
@Table(name = "rental")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Rental implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status")
    private RentalStatus rentalStatus;

    @Column(name = "late_fee")
    private Long lateFee;

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL,orphanRemoval = true) // 고아 객체 제거 -> rental에서 컬렉션의 객체 삭제시, 해당 컬렉션의 entity삭제
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RentedItem> rentedItems = new HashSet<>();

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL,orphanRemoval = true) // 고아 객체 제거 -> rental에서 컬렉션의 객체 삭제시, 해당 컬렉션의 entity삭제
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OverdueItem> overdueItems = new HashSet<>();

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL,orphanRemoval = true) // 고아 객체 제거 -> rental에서 컬렉션의 객체 삭제시, 해당 컬렉션의 entity삭제
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ReturnedItem> returnedItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Rental userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RentalStatus getRentalStatus() {
        return rentalStatus;
    }

    public Rental rentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
        return this;
    }

    public void setRentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public Long getLateFee() {
        return lateFee;
    }

    public Rental lateFee(Long lateFee) {
        this.lateFee = lateFee;
        return this;
    }

    public void setLateFee(Long lateFee) {
        this.lateFee = lateFee;
    }

    public Set<RentedItem> getRentedItems() {
        return rentedItems;
    }

    public Rental rentedItems(Set<RentedItem> rentedItems) {
        this.rentedItems = rentedItems;
        return this;
    }

    public Rental addRentedItem(RentedItem rentedItem) {
        this.rentedItems.add(rentedItem);
        rentedItem.setRental(this);
        return this;
    }

    public Rental removeRentedItem(RentedItem rentedItem) {
        this.rentedItems.remove(rentedItem);
        rentedItem.setRental(null);
        return this;
    }

    public void setRentedItems(Set<RentedItem> rentedItems) {
        this.rentedItems = rentedItems;
    }

    public Set<OverdueItem> getOverdueItems() {
        return overdueItems;
    }

    public Rental overdueItems(Set<OverdueItem> overdueItems) {
        this.overdueItems = overdueItems;
        return this;
    }

    public Rental addOverdueItem(OverdueItem overdueItem) {
        this.overdueItems.add(overdueItem);
        overdueItem.setRental(this);
        return this;
    }

    public Rental removeOverdueItem(OverdueItem overdueItem) {
        this.overdueItems.remove(overdueItem);
        overdueItem.setRental(null);
        return this;
    }

    public void setOverdueItems(Set<OverdueItem> overdueItems) {
        this.overdueItems = overdueItems;
    }

    public Set<ReturnedItem> getReturnedItems() {
        return returnedItems;
    }

    public Rental returnedItems(Set<ReturnedItem> returnedItems) {
        this.returnedItems = returnedItems;
        return this;
    }

    public Rental addReturnedItem(ReturnedItem returnedItem) {
        this.returnedItems.add(returnedItem);
        returnedItem.setRental(this);
        return this;
    }

    public Rental removeReturnedItem(ReturnedItem returnedItem) {
        this.returnedItems.remove(returnedItem);
        returnedItem.setRental(null);
        return this;
    }

    public void setReturnedItems(Set<ReturnedItem> returnedItems) {
        this.returnedItems = returnedItems;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rental)) {
            return false;
        }
        return id != null && id.equals(((Rental) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Rental{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", rentalStatus='" + getRentalStatus() + "'" +
            ", lateFee=" + getLateFee() +
            "}";
    }
    //rental 엔티티 생성 메서드 //
    public static Rental createRental(Long userId){
        Rental rental = new Rental();
        rental.setUserId(userId); // 사용자 일련번호 추가
        // 대출이 가능하도록 상태를 변경
        rental.setRentalStatus(RentalStatus.OK);
        rental.setRentedItems(new HashSet<>());
        rental.setOverdueItems(new HashSet<>());
        rental.setReturnedItems(new HashSet<>());
        rental.setLateFee((long)0); // 연체료 초기화
        return rental;
    }



    //대여하기 메소드//
//    public Rental rentBooks(List<Long> bookIds){
//        if(checkRentalAvailable(bookIds.size())){
//            for(Long bookId : bookIds){
//                RentedItem rentedItem = RentedItem.createRentedItem(this, bookId, LocalDate.now());
//                this.addRentedItem(rentedItem);
//            }
//            this.setRentalStatus(RentalStatus.RENTED);
//            this.setLateFee((long)0);
//            return this;
//
//        }else{
//            return null;
//        }
//    }
    //대출처리 메서드
    public Rental rentBooks(Long bookId, String title){
        this.addRentedItem(RentedItem.createRentedItem(bookId,title,LocalDate.now()));
        return this;
    }

    //반납 처리 메서드
    public Rental returnBooks(Long bookId){
        RentedItem rentedItem = this.rentedItems
            .stream()
            .filter(item -> item.getBookId().equals(bookId)).findFirst().get(); // 대여한 책 들 중 해당 book id 를 갖는 대출도서를 찾는 stream 문법, 이때 findFirst 를 통해 가장 첫번째로 나오는 걸 꺼낸다.
        this.removeRentedItem(rentedItem);
        return this;
    }


//    //대출 가능 여부 체크
//    public boolean checkRentalAvailable(Integer newBookCnt){
//        if(this.rentalStatus!=RentalStatus.OVERDUE){
//            if(this.rentedItems.size()+newBookCnt >5){
//                System.out.println("대출 가능한 도서의 수는 "+( 5- this.getRentedItems().size())+"권 입니다.");
//                return false;
//            }else{
//                return true;
//            }
//        }else{
//            System.out.println("연체 상태입니다.");
//            return false;
//        }
//    }
    //대출 가능 여부 체크 (초기 버전)
    public boolean checkRentalAvailable() throws Exception{
        if(this.rentalStatus.equals(RentalStatus.RENT_AVAILABLE)||this.getLateFee()!=0){
            throw new Exception("연체 상태입니다. 연체료 정산후 도서를 대출하실 수 있습니다.");

        }
        if (this.getRentedItems().size()>=5){
            throw new Exception("대출 가능 도서수는 " +(5-this.getRentedItems().size()) + "권 입니다");
        }
        return true;
    }
}
