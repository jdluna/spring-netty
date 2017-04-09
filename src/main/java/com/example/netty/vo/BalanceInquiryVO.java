package com.example.netty.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.example.netty.core.j8583.Iso8583;

public class BalanceInquiryVO implements Iso8583 {

	private BigDecimal transactionAmount;
	private Date transactionDate;
	private Double conversionRate;

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(Double conversionRate) {
		this.conversionRate = conversionRate;
	}

	@Override
	public String toString() {
		return "BalanceInquiryVO [transactionAmount=" + transactionAmount + ", transactionDate=" + transactionDate
				+ ", conversionRate=" + conversionRate + "]";
	}
}
