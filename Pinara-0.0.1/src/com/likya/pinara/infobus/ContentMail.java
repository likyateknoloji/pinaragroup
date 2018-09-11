package com.likya.pinara.infobus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.likya.pinara.utils.MailContentHelper;
import com.likya.pinara.utils.MailContentHelper.MailSubjectType;

public class ContentMail extends MultipartMail {

	public ContentMail(String mailType, String content) {
		try {
			// Instance name yanlış, artık tek bir senaryo yok !
			setMailSubject("Log Analyzer Action : " + mailType);
			setMultipart(prepare(content));
			setMAIL_TYPE(PinaraMail.MULTIPART);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private Multipart prepare(String content) throws MessagingException, URISyntaxException {
		
		MimeMultipart multipart = new MimeMultipart("related");
	
		// HTML part
		MimeBodyPart mimeBodyPartHtml = new MimeBodyPart();
		// String localizedMessage = Pinara.getMessage("EndOfCycleMail.8"); //$NON-NLS-1$
		String mailHtml = MailContentHelper.getHTMLMailForInfo(MailSubjectType.LOGANALYZE, content);
		
		mimeBodyPartHtml.setText(mailHtml, "utf-8", "html");
		
		// Image part
		MimeBodyPart imagePart = new MimeBodyPart();
		MimeBodyPart imagePart2 = new MimeBodyPart();
		try {
			String imgUrl = "/com/likya/pinara/resources/img/mail-likya.jpg";
			URL url = this.getClass().getResource(imgUrl);
			imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(url.openStream(), "image/jpg")));
			
			String imgUrl2 = "/com/likya/pinara/resources/img/mail-divider.png";
			URL url2 = this.getClass().getResource(imgUrl2);
			imagePart2.setDataHandler(new DataHandler(new ByteArrayDataSource(url2.openStream(), "image/png")));
			// imagePart.attachFile("/com/likya/pinara/resources/likya_mail.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		imagePart.setHeader("Content-ID", "<likyajpg10976@likyateknoloji.com>");
		imagePart2.setHeader("Content-ID", "<dividerpng10977@likyateknoloji.com>");
		imagePart.setDisposition(MimeBodyPart.INLINE);
		imagePart2.setDisposition(MimeBodyPart.INLINE);
		
		multipart.addBodyPart(mimeBodyPartHtml);
		multipart.addBodyPart(imagePart);
		multipart.addBodyPart(imagePart2);
		
		return multipart;
		
	}

}
