<?xml version="1.0" encoding="UTF-8"?>

        <?import javafx.scene.control.Button?>
        <?import javafx.scene.control.Label?>
        <?import javafx.scene.control.PasswordField?>
        <?import javafx.scene.control.TextField?>
        <?import javafx.scene.image.Image?>
        <?import javafx.scene.image.ImageView?>
        <?import javafx.scene.layout.AnchorPane?>
        <?import javafx.scene.shape.Line?>
        <?import javafx.scene.shape.Rectangle?>
        <?import javafx.scene.text.Font?>

<AnchorPane fx:id="root" maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="700.0" prefWidth="1000.0" xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="sample.LoginController">
<Rectangle arcHeight="5.0" arcWidth="5.0" fill="WHITE" height="700.0" layoutX="525.0" stroke="WHITE" strokeType="INSIDE" width="475.0" />
<ImageView fitHeight="700.0" fitWidth="525.0" pickOnBounds="true" preserveRatio="true">
<image>
<Image url="@Resources/derek-liang-MuDLzGMU06c-unsplash.jpg" />
</image>
</ImageView>
<Label layoutX="637.0" layoutY="241.0" text="Sign In To Your Account">
<font>
<Font name="Candara Bold" size="24.0" />
</font>
</Label>
<Line endX="172.39996337890625" layoutX="734.0" layoutY="326.0" startX="-100.0" />
<Line endX="172.39996337890625" layoutX="734.0" layoutY="377.0" startX="-100.0" />
<TextField fx:id="userName" focusTraversable="false" layoutX="633.0" layoutY="298.0" prefHeight="26.0" prefWidth="273.0" promptText="Email or Username" styleClass="custom-text-field-2" stylesheets="@sample.css">
<font>
<Font name="Arial Rounded MT Bold" size="14.0" />
</font>
</TextField>
<PasswordField fx:id="passwordField" focusTraversable="false" layoutX="634.0" layoutY="350.0" promptText="Password" styleClass="custom-text-field-2" stylesheets="@sample.css">
<font>
<Font name="Arial Rounded MT Bold" size="14.0" />
</font>
</PasswordField>
<Button layoutX="629.0" layoutY="426.0" mnemonicParsing="false" onAction="#login" prefHeight="18.0" prefWidth="131.0" styleClass="round-gray" stylesheets="@transparent.css" text="Login">
<font>
<Font name="System Bold" size="14.0" />
</font>
</Button>
<Label layoutX="635.0" layoutY="111.0" text="Welcome to">
<font>
<Font name="Candara Bold" size="24.0" />
</font>
</Label>
<Rectangle arcHeight="5.0" arcWidth="5.0" fill="#4a97df" height="3.0" layoutX="638.0" layoutY="205.0" stroke="#4a97df" strokeLineCap="ROUND" strokeLineJoin="ROUND" strokeMiterLimit="0.0" width="113.0" />
<Rectangle arcHeight="5.0" arcWidth="5.0" fill="#d0d7dc" height="3.0" layoutX="761.0" layoutY="205.0" stroke="#d0d7dc" strokeLineCap="ROUND" strokeLineJoin="ROUND" strokeMiterLimit="0.0" width="113.0" />
<ImageView fitHeight="236.0" fitWidth="304.0" layoutX="763.0" layoutY="464.0" pickOnBounds="true" preserveRatio="true">
<image>
<Image url="@Resources/OrganicLogo.png" />
</image>
</ImageView>
<Label layoutX="636.0" layoutY="166.0" text="We make it wasy for everyone to maximize " textFill="GREY">
<font>
<Font name="Arial Bold" size="11.0" />
</font>
</Label>
<Label layoutX="636.0" layoutY="180.0" text="their investment." textFill="GREY">
<font>
<Font name="Arial Bold" size="11.0" />
</font>
</Label>
<Button fx:id="forgotPasswordButton" layoutX="803.0" layoutY="429.0" mnemonicParsing="false" onAction="#forgetPasswordPushed" stylesheets="@sample.css" text="Forget Password" textFill="#5a879a" underline="true" />
</AnchorPane>
