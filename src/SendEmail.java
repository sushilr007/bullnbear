import java.util.Properties;    
import javax.mail.*;    
import javax.mail.internet.*;    
import java.util.*;
import javax.activation.*;

public class SendEmail {

    SendEmail(String pwd,String email) {
        // Recipient's email ID needs to be mentioned.
          //String to = "akadmllu@gmail.com";

          // Sender's email ID needs to be mentioned
          String from = "sonuamit282@gmail.com";


          // Get system properties
          Properties properties = System.getProperties();

          properties.put("mail.smtp.starttls.enable", "true"); 
          properties.put("mail.smtp.host", "smtp.gmail.com");

          properties.put("mail.smtp.port", "587");
          properties.put("mail.smtp.auth", "true");
          Authenticator authenticator = new Authenticator () {
                public PasswordAuthentication getPasswordAuthentication(){
                    //return new PasswordAuthentication("sonuamit282@gmail.com","@mIt7875644278");//userid and password for "from" email address 
                	return new PasswordAuthentication("sonuamit282@gmail.com","@mIt7875644278");//userid and password for "from" email address 

                }
            };

            Session session = Session.getDefaultInstance( properties , authenticator);
           // Session session = Session.getDefaultInstance( properties);
          try{
             // Create a default MimeMessage object.
             MimeMessage message = new MimeMessage(session);

             // Set From: header field of the header.
             message.setFrom(new InternetAddress(from));

             // Set To: header field of the header.
             message.addRecipient(Message.RecipientType.TO,
                                      new InternetAddress(email));

             // Set Subject: header field
             message.setSubject("Password for your account!");

             // Now set the actual message
             message.setText("Password is : "+pwd);

             // Send message
             Transport.send(message);
             System.out.println("Sent message successfully....");
          }catch (MessagingException mex) {
             mex.printStackTrace();
          }
    }

}

