/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.krawler.hql.accounting;

import com.krawler.common.admin.Company;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class ProductBuild {
    private String ID;
    private String refno;
    private Date entryDate;
    private String memo;
    private int quantity;
    private Company company;
    private Product product;
    private JournalEntry journalentry;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public JournalEntry getJournalentry() {
        return journalentry;
    }

    public void setJournalentry(JournalEntry journalentry) {
        this.journalentry = journalentry;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }
}
