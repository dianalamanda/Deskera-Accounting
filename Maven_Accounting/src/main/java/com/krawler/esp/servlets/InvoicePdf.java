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
package com.krawler.esp.servlets;

import com.krawler.common.admin.Company;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.hql.accounting.JournalEntryDetail;
import com.lowagie.text.DocumentException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.ProfileHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hql.accounting.Account;
import com.krawler.hql.accounting.CreditNote;
import com.krawler.hql.accounting.CreditNoteDetail;
import com.krawler.hql.accounting.Customer;
import com.krawler.hql.accounting.DebitNote;
import com.krawler.hql.accounting.DebitNoteDetail;
import com.krawler.hql.accounting.Discount;
import com.krawler.hql.accounting.GoodsReceipt;
import com.krawler.hql.accounting.GoodsReceiptDetail;
import com.krawler.hql.accounting.Payment;
import com.krawler.hql.accounting.PaymentDetail;
import com.krawler.hql.accounting.PurchaseOrder;
import com.krawler.hql.accounting.PurchaseOrderDetail;
import com.krawler.hql.accounting.SalesOrder;
import com.krawler.hql.accounting.SalesOrderDetail;
import com.krawler.hql.accounting.StaticValues;
import com.krawler.hql.accounting.Vendor;
import com.krawler.utils.json.base.JSONException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Set;

public class InvoicePdf extends HttpServlet {

//    private static Font fontSmallRegular = FontFactory.getFont("Helvetica", 9, Font.NORMAL, Color.BLACK);
//    private static Font fontMediumBold = FontFactory.getFont("Helvetica", 11, Font.BOLD, Color.BLACK);
//    private static Font fontTblMediumBold = FontFactory.getFont("Helvetica", 9, Font.BOLD, Color.BLACK);
//    private static Font fontBig = FontFactory.getFont("Helvetica", 22, Font.UNDERLINE, Color.BLACK);
//    private static String imgPath = "";
//
//    private static void addHeaderFooter(Document document,PdfWriter writer) throws DocumentException,ServiceException{
//        fontSmallRegular.setColor(Color.BLACK);
//        java.util.Date dt = new java.util.Date();
//        String date="yyyy-MM-dd";
//        java.text.SimpleDateFormat dtf = new java.text.SimpleDateFormat(date);
//        String DateStr = dtf.format(dt);
//        PdfPTable footer = new PdfPTable(1);
//        PdfPCell footerSeparator = new PdfPCell(new Phrase(""));
//        footerSeparator.setBorder(PdfPCell.BOX);
//        footerSeparator.setPadding(0);
//        footerSeparator.setColspan(3);
//        footer.addCell(footerSeparator);
//
//        String PageDate = DateStr;
//        PdfPCell pagerDateCell = new PdfPCell(new Phrase("Generated Date : "+ PageDate, fontSmallRegular));
//        pagerDateCell.setBorder(0);
//        pagerDateCell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
//        footer.addCell(pagerDateCell);
//        // -------- footer end   -----------
//       try {
//        Rectangle page = document.getPageSize();
//        footer.setTotalWidth(page.getWidth()-document.leftMargin()-document.rightMargin());
//        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() ,writer.getDirectContent());
//    } catch (Exception e) {
//        throw new ExceptionConverter(e);
//    }
//    }
//
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, ServiceException, IOException {
//        if (com.krawler.esp.handlers.SessionHandler.isValidSession(request, response)) {
//            Session session = null;
//            try {
//                session = HibernateUtil.getCurrentSession();
//                double amount=Double.parseDouble(request.getParameter("amount"));
//                int mode =Integer.parseInt(request.getParameter("mode"));
//                String billid=request.getParameter("bills");
//                String logoPath=ProfileImageServlet.getProfileImagePath(request, true,"images/deskera-accounting-big-without-username.gif");
//                String currencyid=AuthHandler.getCurrencyID(request);
//                DateFormat formatter=AuthHandler.getUserDateFormatter(session, AuthHandler.getDateFormatID(request), AuthHandler.getTimeZoneDifference(request),true);
//                ByteArrayOutputStream baos=createPdf(session, currencyid, billid, formatter, mode,amount,getServletContext().getRealPath(""),logoPath);
//                response.setHeader("Content-Disposition", "attachment; filename=\"" + AuthHandler.getCompanyName(request) + ".pdf\"");
//                response.setContentType("application/octet-stream");
//                response.setContentLength(baos.size());
//                response.getOutputStream().write(baos.toByteArray());
//                response.getOutputStream().flush();
//                response.getOutputStream().close();
//            } catch (Exception ex) {
//                throw ServiceException.FAILURE(ex.getMessage(), ex);
//            } finally {
//                HibernateUtil.closeSession(session);
//            }
//        } else {
//            response.getOutputStream().println("{\"valid\": false}");
//        }
//    }public static ByteArrayOutputStream createPdf(Session session, String currencyid, String billid, DateFormat formatter, int mode, double amount, String contextpath, String logoPath) throws JSONException {
//        ByteArrayOutputStream baos=null;
//        try {
//            if(mode == StaticValues.AUTONUM_GOODSRECEIPT) {
//                GoodsReceipt gRec = (GoodsReceipt) session.get(GoodsReceipt.class, billid);
//                String[] header1 = {"DATE","GR NO"};
//                String[] header2 = {"VENDOR","AMOUNT DUE","MEMO"};
//                String[] header3 = {"PRODUCT","QUANTITY","RATE","DISCOUNT","AMOUNT"};
//                String[] header4 = {"TERMS","DUE DATE"};
//                String[] values1 = {formatter.format(gRec.getJournalEntry().getEntryDate()),gRec.getGoodsReceiptNumber()};
//                String[] values2 = {gRec.getVendorEntry().getAccount().getName(),ProfileHandler.getFormattedCurrency(amount,currencyid, session),gRec.getMemo()};
//                String[] values3 = new String[5];
//                String[] values4={Long.toString((gRec.getDueDate().getTime()-gRec.getJournalEntry().getEntryDate().getTime())/(3600000*24))+" Days",formatter.format(gRec.getDueDate())};
//                baos=createForm(session, currencyid, header1, header2, header3,header4, values1, values2, values3,values4, "GoodsReceipt", gRec, gRec.getCompany(),contextpath,logoPath,formatter);
//            }else if(mode==StaticValues.AUTONUM_DEBITNOTE){
//                DebitNote deb = (DebitNote) session.get(DebitNote.class, billid);
//                Set<JournalEntryDetail> entryset=deb.getJournalEntry().getDetails();
//                Vendor vendor=new Vendor();
//                Iterator itr=entryset.iterator();
//                while(itr.hasNext()){
//                    Account acc=((JournalEntryDetail)itr.next()).getAccount();
//                    vendor=(Vendor)session.get(Vendor.class,acc.getID());
//                    if(vendor!=null)break;
//                }
//                String[] header1 = {"DATE","DEBIT NO"};
//                String[] header2 = {"VENDOR","AMOUNT","MEMO"};
//                String[] header3 = {"PRODUCT","QUANTITY","DISCOUNT AMOUNT"};
//                String[] header4 =new String[0];
//                String[] values1 = {formatter.format(deb.getJournalEntry().getEntryDate()),deb.getDebitNoteNumber()};
//                String[] values2 = {vendor.getName(),ProfileHandler.getFormattedCurrency(amount,currencyid, session),deb.getMemo()};
//                String[] values3 = new String[3];
//                String[] values4 = new String[0];
//                baos=createForm(session, currencyid, header1, header2, header3,header4, values1, values2, values3,values4, "debit", deb, deb.getCompany(),contextpath,logoPath,formatter);
//            }else if(mode==StaticValues.AUTONUM_CREDITNOTE){
//                CreditNote cre = (CreditNote) session.get(CreditNote.class, billid);
//                Set<JournalEntryDetail> entryset=cre.getJournalEntry().getDetails();
//                Customer customer=new Customer();
//                Iterator itr=entryset.iterator();
//                while(itr.hasNext()){
//                    Account acc=((JournalEntryDetail)itr.next()).getAccount();
//                    customer=(Customer)session.get(Customer.class,acc.getID());
//                    if(customer!=null)break;
//                }
//                String[] header1 = {"DATE","CREDIT NO"};
//                String[] header2 = {"CUSTOMER","AMOUNT","MEMO"};
//                String[] header3 = {"PRODUCT","QUANTITY","DISCOUNT AMOUNT"};
//                String[] header4 =new String[0];
//                String[] values1 = {formatter.format(cre.getJournalEntry().getEntryDate()),cre.getCreditNoteNumber()};
//                String[] values2 = {customer.getName(),ProfileHandler.getFormattedCurrency(amount,currencyid, session),cre.getMemo()};
//                String[] values3 = new String[3];
//                String[] values4 = new String[0];
//                baos=createForm(session, currencyid, header1, header2, header3,header4,values1, values2, values3,values4, "credit", cre, cre.getCompany(),contextpath, logoPath,formatter);
//            }else if (mode == StaticValues.AUTONUM_PURCHASEORDER) {
//                PurchaseOrder po = (PurchaseOrder) session.get(PurchaseOrder.class, billid);
//                String[] header1 = {"DATE","PO NO"};
//                String[] header2 = {"VENDOR","AMOUNT DUE","MEMO"};
//                String[] header3 = {"PRODUCT","QUANTITY","RATE","DISCOUNT","AMOUNT"};
//                String[] header4 = {"TERMS","DUE DATE"};
//                String[] values1 = {formatter.format(po.getOrderDate()),po.getPurchaseOrderNumber()};
//                String[] values2 = {po.getVendor().getName(),ProfileHandler.getFormattedCurrency(amount,currencyid, session),po.getMemo()};
//                String[] values3 = new String[5];
//                String[] values4={Long.toString((po.getDueDate().getTime()-po.getOrderDate().getTime())/(3600000*24))+" Days",formatter.format(po.getDueDate())};
//                baos=createForm(session, currencyid, header1, header2, header3,header4, values1, values2, values3,values4, "PurchaseOrder", po, po.getCompany(),contextpath,logoPath,formatter);
//            }else if (mode == StaticValues.AUTONUM_SALESORDER) {
//                SalesOrder so = (SalesOrder) session.get(SalesOrder.class, billid);
//                String[] header1 = {"DATE","SO NO"};
//                String[] header2 = {"CUSTOMER","AMOUNT DUE","MEMO"};
//                String[] header3 = {"PRODUCT","QUANTITY","RATE","DISCOUNT","AMOUNT"};
//                String[] header4 = {"TERMS","DUE DATE"};
//                String[] values1 = {formatter.format(so.getOrderDate()),so.getSalesOrderNumber()};
//                String[] values2 = {so.getCustomer().getName(),ProfileHandler.getFormattedCurrency(amount,currencyid, session),so.getMemo()};
//                String[] values3 = new String[5];
//                String[] values4={Long.toString((so.getDueDate().getTime()-so.getOrderDate().getTime())/(3600000*24))+" Days",formatter.format(so.getDueDate())};
//                baos=createForm(session, currencyid, header1, header2, header3,header4, values1, values2, values3,values4,"SalesOrder", so, so.getCompany(),contextpath,logoPath,formatter);
//            }else if (mode == StaticValues.AUTONUM_PAYMENT) {
//                Payment pay = (Payment) session.get(Payment.class, billid);
//                Set<JournalEntryDetail> entryset=pay.getJournalEntry().getDetails();
//                Vendor vendor=new Vendor();
//                Iterator itr=entryset.iterator();
//                while(itr.hasNext()){
//                    Account acc=((JournalEntryDetail)itr.next()).getAccount();
//                    vendor=(Vendor)session.get(Vendor.class,acc.getID());
//                    if(vendor!=null)break;
//                }
//                String[] header1 = {"DATE","PAYMENT NO"};
//                String[] header2 = {"VENDOR","AMOUNT","MEMO"};
//                String[] header3 = {"GOODRECEIPT NO","CREATION DATE","DUE DATE","AMOUNT PAID"};
//                String[] header4 = new String[0];
//                String[] values1 = {formatter.format(pay.getJournalEntry().getEntryDate()),pay.getPaymentNumber()};
//                String[] values2 = {vendor.getName(),ProfileHandler.getFormattedCurrency(amount,currencyid, session),pay.getMemo()};
//                String[] values3 = new String[4];
//                String[] values4=new String[0];
//                baos=createForm(session, currencyid, header1, header2, header3,header4, values1, values2, values3,values4, "Payment", pay, pay.getCompany(),contextpath,logoPath,formatter);
//            }
//        }catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return baos;
//    }
//
//    private static ByteArrayOutputStream createForm(Session session, String currencyid, String[] header1, String[] header2, String[] header3,String[] header4, String[] values1, String[] values2, String[] values3,String[] values4, String string, Object ob, Company com, String contextpath, String logoPath,DateFormat formatter) throws JSONException, DocumentException, SessionExpiredException, ServiceException, IOException {
//        ByteArrayOutputStream baos=null;
//        Document document = null;
//        PdfWriter writer = null;
//        try {
//        String company[] = new String[3];
//        company[0] = com.getCompanyName();
//        company[1] = com.getAddress();
//        company[2] = com.getEmailID();
//        baos=new ByteArrayOutputStream();
//        document = new Document(PageSize.A4, 15, 15, 15, 15);
//        writer = PdfWriter.getInstance(document, baos);
//        document.open();
//        addHeaderFooter(document,writer);
//        PdfPTable tab1 = null;
//        PdfPTable tab2 = null;
//        PdfPTable tab3 = null;
//
//        /*-----------------------------Add Company Name in Center ------------------*/
//        tab1 = new PdfPTable(1);
//        tab1.setHorizontalAlignment(Element.ALIGN_LEFT);
//        PdfPCell cell = new PdfPCell(new Paragraph(com.getCompanyName(), fontBig));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setBorder(0);
//        tab1.addCell(cell);
//
//        tab2 = new PdfPTable(1);
//        tab2.setHorizontalAlignment(Element.ALIGN_LEFT);
//        imgPath = logoPath;
//        PdfPCell imgCell = null;
//        try {
//            Image img = Image.getInstance(imgPath);
//            imgCell = new PdfPCell(img);
//        } catch (Exception e) {
//            imgCell = new PdfPCell(new Paragraph(com.getCompanyName(), fontBig));
//        }
//        imgCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        imgCell.setBorder(0);
//        tab2.addCell(imgCell);
//
//        PdfPTable table=new PdfPTable(2);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{40,60});
//        PdfPCell cellCompimg = new PdfPCell(tab2);
//        cellCompimg.setBorder(0);
//        table.addCell(cellCompimg);
//        PdfPCell cellCompname = new PdfPCell(tab1);
//        cellCompname.setBorder(0);
//        table.addCell(cellCompname);
//        document.add(table);
//        document.add(new Paragraph("\n\n\n"));
//
//        /*-----------------------------Add Company information and Invoice Info------------------*/
//        PdfPTable table2 = new PdfPTable(3);
//        table2.setWidthPercentage(100);
//        if(header4.length!=0)
//            table2.setWidths(new float[]{40,30,30});
//        else
//            table2.setWidths(new float[]{50,50,0});
//        tab1 = getCompanyInfo(company);
//        tab2 = createTable(header1, values1);
//        PdfPCell cell1 = new PdfPCell(tab1);
//        cell1.setBorder(0);
//        table2.addCell(cell1);
//        PdfPCell cell2 = new PdfPCell(tab2);
//        cell2.setBorder(1);
//        table2.addCell(cell2);
//        PdfPCell cel =new PdfPCell();
//        if(header4.length!=0){
//            tab3 = createTable(header4, values4);
//            cel = new PdfPCell(tab3);
//        }else
//            cel = new PdfPCell(new Paragraph("", fontSmallRegular));
//        cel.setBorder(1);
//        table2.addCell(cel);
//        document.add(table2);
//        document.add(new Paragraph("\n\n\n"));
//
//        /*-----------------------------Add BillTo Amount Enclosed -------------------------*/
//        PdfPTable table3 = new PdfPTable(1);
//        table3.setWidthPercentage(100);
//        tab1 = createTable(header2, values2);
//        PdfPCell cell3 = new PdfPCell(tab1);
//        cell3.setBorder(1);
//        table3.addCell(cell3);
//        document.add(table3);
//        document.add(new Paragraph("\n\n\n\n\n\n"));
//
//        /*-----------------------------Add Cutting Line -------------------------*/
//        PdfPTable table4 = new PdfPTable(1);
//        imgPath = contextpath+"/images/pdf-cut.jpg";
//        table4.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table4.setWidthPercentage(100);
//        PdfPCell cell11 = null;
//        try {
//            Image img = Image.getInstance(imgPath);
//            img.scalePercent(35);
//            cell11 = new PdfPCell(img);
//        } catch (Exception e) {
//        }
//        cell11.setBorder(0);
//        table4.addCell(cell11);
//        document.add(table4);
//        document.add(new Paragraph("\n\n"));
//
//        /*-----------------------------Add Product Information ------------------*/
//        PdfPTable table5 = new PdfPTable(1);
//        table5.setWidthPercentage(100);
//        if(string.equals("GoodsReceipt")){
//            GoodsReceipt gr = (GoodsReceipt) ob;
//            tab1 = createGoodsReceiptTable(header3, values3, currencyid, gr, session);
//        }else if(string.equals("debit")){
//            DebitNote dn = (DebitNote) ob;
//            tab1 = createDebitTable(header3, values3, currencyid, dn, session);
//        }else if(string.equals("credit")){
//            CreditNote cn = (CreditNote) ob;
//            tab1 = createCreditTable(header3, values3, currencyid, cn, session);
//        }else if(string.equals("PurchaseOrder")){
//            PurchaseOrder po = (PurchaseOrder) ob;
//            tab1 = createPurchaseOrderTable(header3, values3, currencyid, po, session);
//        }else if(string.equals("SalesOrder")){
//            SalesOrder so = (SalesOrder) ob;
//            tab1 = createSalesOrderTable(header3, values3, currencyid, so, session);
//        }else if(string.equals("Payment")){
//            Payment pay = (Payment) ob;
//            tab1 = createPaymentTable(header3, values3, currencyid, pay, session,formatter);
//        }
//        PdfPCell cell5 = new PdfPCell(tab1);
//        cell5.setBorder(1);
//        table5.addCell(cell5);
//        document.add(table5);
//        document.add(new Paragraph("\n\n\n"));
//
//        /*-----------------------------Download file ------------------*/
//        return baos;
//        } catch (Exception ex) {
//            return null;
//        } finally {
//            if(document!=null)
//                document.close();
//            if(writer!=null)
//                writer.close();
//            if(baos!=null)
//                baos.close();
//        }
//    }
//
//    private static PdfPTable createDebitTable(String header[], String values[], String currencyid, DebitNote deb, Session session) throws DocumentException, ServiceException, JSONException, HibernateException, SessionExpiredException {
//        PdfPTable table = new PdfPTable(header.length);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{30, 30, 40});
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//        Iterator itr = deb.getRows().iterator();
//            while (itr.hasNext()) {
//                DebitNoteDetail debDet = (DebitNoteDetail) itr.next();
//                cell = new PdfPCell(new Paragraph(debDet.getGoodsReceiptRow().getInventory().getProduct().getName(), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell = new PdfPCell(new Paragraph(Integer.toString(debDet.getQuantity()) + " " + debDet.getGoodsReceiptRow().getInventory().getProduct().getUnitOfMeasure().getName(), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell=calculateDiscount(debDet.getDiscount(),currencyid,session);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//            }
//         for (int i = 0; i < 3; i++) {
//            PdfPCell cell1 = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell1.setBorder(Rectangle.TOP);
//            table.addCell(cell1);
//        }
//        return table;
//    }
//
//    private static PdfPTable createCreditTable(String header[], String values[], String currencyid, CreditNote cre, Session session) throws DocumentException, ServiceException, JSONException, HibernateException, SessionExpiredException {
//        PdfPTable table = new PdfPTable(header.length);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{30, 30, 40});
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//           Iterator itr = cre.getRows().iterator();
//            while (itr.hasNext()) {
//                CreditNoteDetail creDet = (CreditNoteDetail) itr.next();
//                cell = new PdfPCell(new Paragraph(creDet.getInvoiceRow().getInventory().getProduct().getName(), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell = new PdfPCell(new Paragraph(Integer.toString(creDet.getQuantity()) + " " + creDet.getInvoiceRow().getInventory().getProduct().getUnitOfMeasure().getName(), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell=calculateDiscount(creDet.getDiscount(),currencyid,session);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//            }
//        for (int i = 0; i < 3; i++) {
//            PdfPCell cell1 = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell1.setBorder(Rectangle.TOP);
//            table.addCell(cell1);
//        }
//        return table;
//    }
//
//    private static PdfPTable createGoodsReceiptTable(String[] header, String[] values, String currencyid, GoodsReceipt gr, Session session) throws HibernateException, SessionExpiredException, DocumentException {
//        double total = 0;
//        PdfPTable table = new PdfPTable(header.length);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{30, 15, 15, 20, 20});
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//            Iterator itr = gr.getRows().iterator();
//            while (itr.hasNext()) {
//                GoodsReceiptDetail row = (GoodsReceiptDetail) itr.next();
//                cell = new PdfPCell(new Paragraph(row.getInventory().getProduct().getName(), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell = new PdfPCell(new Paragraph(Integer.toString(row.getInventory().getQuantity()) + " " + row.getInventory().getProduct().getUnitOfMeasure().getName(), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(row.getRate(), currencyid, session), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                cell=calculateDiscount(row.getDiscount(),currencyid,session);
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//                double amount = row.getRate() * row.getInventory().getQuantity();
//                if (row.getDiscount() != null) {
//                    amount -= row.getDiscount().getDiscountValue();
//                }
//                total += amount;
//                cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(amount, currencyid, session), fontSmallRegular));
//                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//                table.addCell(cell);
//            }
//
//        for (int j = 0; j < 50; j++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//        }
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.TOP);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  SUB TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(total, currencyid, session), fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        cell.setPadding(4);
//        table.addCell(cell);
//
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  DISCOUNT", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setPadding(4);
//        table.addCell(cell);
//        cell=calculateDiscount(gr.getDiscount(),currencyid,session);
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  SHIPPING CHARGES", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell= getCharges(gr.getShipEntry(),currencyid,session);
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  OTHER CHARGES", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell= getCharges(gr.getOtherEntry(),currencyid,session);
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(gr.getVendorEntry().getAmount(),currencyid, session) ,fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        return table;
//    }
//
//
//    private static PdfPTable createPurchaseOrderTable(String[] header, String[] values, String currencyid, PurchaseOrder po, Session session) throws HibernateException, SessionExpiredException, DocumentException {
//        double total = 0;
//        PdfPTable table = new PdfPTable(header.length);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{30, 15, 15, 20, 20});
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//        Iterator itr = po.getRows().iterator();
//        while (itr.hasNext()) {
//            PurchaseOrderDetail row = (PurchaseOrderDetail) itr.next();
//            cell = new PdfPCell(new Paragraph(row.getProduct().getName(), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(Integer.toString(row.getQuantity()) + " " + row.getProduct().getUnitOfMeasure().getName(), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(row.getRate(), currencyid, session), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph("--", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            double amount = row.getRate() * row.getQuantity();
//            total += amount;
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(amount, currencyid, session), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//        }
//
//        for (int j = 0; j < 50; j++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//        }
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.TOP);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  SUB TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(total, currencyid, session), fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  DISCOUNT", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph("--", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(total, currencyid, session), fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        return table;
//    }
//
//     private static PdfPTable createSalesOrderTable(String[] header, String[] values, String currencyid, SalesOrder so, Session session) throws DocumentException, HibernateException, SessionExpiredException {
//        double total = 0;
//        PdfPTable table = new PdfPTable(header.length);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{30, 15, 15, 20, 20});
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//        Iterator itr = so.getRows().iterator();
//        while (itr.hasNext()) {
//            SalesOrderDetail row = (SalesOrderDetail) itr.next();
//            cell = new PdfPCell(new Paragraph(row.getProduct().getName(), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(Integer.toString(row.getQuantity()) + " " + row.getProduct().getUnitOfMeasure().getName(), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(row.getRate(), currencyid, session), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph("--", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            double amount = row.getRate() * row.getQuantity();
//            total += amount;
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(amount, currencyid, session), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//        }
//
//        for (int j = 0; j < 50; j++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//        }
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.TOP);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  SUB TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(total, currencyid, session), fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  DISCOUNT", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph("--", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        for (int i = 0; i < 3; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(0);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(total, currencyid, session), fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        return table;
//    }
//
//    private static PdfPTable createPaymentTable(String[] header, String[] values, String currencyid, Payment pay, Session session,DateFormat formatter) throws DocumentException, HibernateException, SessionExpiredException {
//        double total = 0;
//        PdfPTable table = new PdfPTable(header.length);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{30,25, 25, 20});
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//        Iterator itr = pay.getRows().iterator();
//        while (itr.hasNext()) {
//            PaymentDetail row = (PaymentDetail) itr.next();
//            cell = new PdfPCell(new Paragraph(row.getGoodsReceipt().getGoodsReceiptNumber(), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(formatter.format(row.getGoodsReceipt().getShipDate()), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(formatter.format(row.getGoodsReceipt().getDueDate()), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(row.getAmount(), currencyid, session), fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//            double amount = row.getAmount();
//            total += amount;
//        }
//
//        for (int j = 0; j < 40; j++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
//            table.addCell(cell);
//        }
//        for (int i = 0; i < 2; i++) {
//            cell = new PdfPCell(new Paragraph("", fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBorder(Rectangle.TOP);
//            table.addCell(cell);
//        }
//        cell = new PdfPCell(new Paragraph("  TOTAL", fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        table.addCell(cell);
//        cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(total, currencyid, session), fontTblMediumBold));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        table.addCell(cell);
//        return table;
//    }
//
//    private static PdfPCell calculateDiscount(Discount disc,String currencyid,Session session) throws HibernateException, SessionExpiredException {
//        PdfPCell cell=null;
//        if (disc== null) {
//            cell = new PdfPCell(new Paragraph("--", fontSmallRegular));
//        } else if (disc.isInPercent()) {
//            cell = new PdfPCell(new Paragraph(Double.toString(disc.getDiscount()) + "%", fontSmallRegular));
//        } else {
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(disc.getDiscountValue(),currencyid, session), fontSmallRegular));
//        }
//        return cell;
//    }
//
//    private static PdfPCell getCharges(JournalEntryDetail jEntry,String currencyid,Session session) throws HibernateException, SessionExpiredException {
//        PdfPCell cell=null;
//        if (jEntry== null) {
//            cell = new PdfPCell(new Paragraph("--", fontSmallRegular));
//        }else {
//            cell = new PdfPCell(new Paragraph(ProfileHandler.getFormattedCurrency(jEntry.getAmount(),currencyid, session), fontSmallRegular));
//        }
//        return cell;
//    }
//
//    private static PdfPTable createTable(String header[], String values[]) {
//        PdfPTable table = new PdfPTable(header.length);
//        PdfPCell cell = null;
//        for (int i = 0; i < header.length; i++) {
//            cell = new PdfPCell(new Paragraph(header[i], fontTblMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setPadding(3);
//            table.addCell(cell);
//        }
//        for (int j = 0; j < values.length; j++) {
//            cell = new PdfPCell(new Paragraph(values[j], fontSmallRegular));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(cell);
//        }
//        return table;
//    }
//
//    public static PdfPTable getCompanyInfo(String com[]) {
//        PdfPTable tab1 = new PdfPTable(1);
//        tab1.setHorizontalAlignment(Element.ALIGN_LEFT);
//        for (int i = 0; i < 3; i++) {
//            PdfPCell cell = new PdfPCell(new Paragraph(com[i], fontMediumBold));
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            cell.setBorder(0);
//            tab1.addCell(cell);
//        }
//        return tab1;
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP <code>GET</code> method.
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            processRequest(request, response);
//        } catch (ServiceException ex) {
//            Logger.getLogger(InvoicePdf.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    /**
//     * Handles the HTTP <code>POST</code> method.
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            processRequest(request, response);
//        } catch (ServiceException ex) {
//            Logger.getLogger(InvoicePdf.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
