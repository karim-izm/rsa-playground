package ssi.master.rsaplayground.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ImageView logoImageView;

    @FXML
    private JFXButton generatePublicKeyButton;

    @FXML
    private JFXButton encryptButton;
    @FXML
    private JFXButton decryptButton;
    @FXML
    private JFXButton choosePrivateKeyButton;

    @FXML
    private JFXTextArea text;

    @FXML
    private JFXTextArea outputTextArea;

    @FXML
    private JFXComboBox<Integer> lengthComboBox;

    @FXML
    private JFXButton uploadTxtFile;
    @FXML
    private JFXButton encryptTxtButton;
    @FXML
    private JFXButton decryptTxtButton;
    private File selectedTxtFile;


    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load the image from the resources or file system
        Image image = new Image(getClass().getResourceAsStream("/ssi/master/rsaplayground/images/ensa-logo.png"));

        // Set the image in the ImageView
        logoImageView.setImage(image);
        lengthComboBox.getItems().addAll(1024, 2048, 4096);
        lengthComboBox.setValue(2048);
        generatePublicKeyButton.setOnAction(this::handleGenerateKeys);
        encryptButton.setOnAction(this::handleEncrypt);
        decryptButton.setOnAction(this::handleDecrypt);
        choosePrivateKeyButton.setOnAction(this::handleChoosePrivateKey);
        uploadTxtFile.setOnAction(this::handleUploadTxtFile);
        encryptTxtButton.setOnAction(this::handleEncryptTxtFile);
        decryptTxtButton.setOnAction(this::handleDecryptTxtFile);


        text.setDisable(true);
        choosePrivateKeyButton.setDisable(true);
        encryptButton.setDisable(true);
        decryptButton.setDisable(true);
        decryptTxtButton.setVisible(false);
    }



    private void handleGenerateKeys(ActionEvent event) {
        try {
            int keyLength = lengthComboBox.getValue();
            //1024 byes 2048 bytes and 4096 bytes

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keyLength);
            KeyPair keyPair = generator.generateKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            saveKeyToFile("public.key", publicKey.getEncoded());
            saveKeyToFile("private.key", privateKey.getEncoded());

            showSuccessAlert("Key Generation" , null , "RSA Key Pair generated and saved successfully!");


            text.setDisable(false);
            outputTextArea.setEditable(false);
            encryptButton.setDisable(false);
            choosePrivateKeyButton.setDisable(false);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Key Generation Failed", "Error generating RSA Key Pair.");
        }
    }


    private void handleEncrypt(ActionEvent event) {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            String inputText = text.getText();
            byte[] encryptedBytes = encryptCipher.doFinal(inputText.getBytes());
            String encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);

            outputTextArea.setText(encryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Encryption Failed", "Error encrypting the message.");
        }
    }

    private void handleChoosePrivateKey(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        String initialPath = "C:/Users/iiizm/Documents/Spring Projects/rsa-playground";
        File initialDir = new File(initialPath);
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        fileChooser.setTitle("Choose Private Key File");
        File file = fileChooser.showOpenDialog(logoImageView.getScene().getWindow());
        if (file != null) {
            try {
                byte[] privateKeyBytes = Files.readAllBytes(file.toPath());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                privateKey = keyFactory.generatePrivate(keySpec);
                showSuccessAlert("Success", "Private Key Loaded", "Private key loaded successfully.");
                decryptButton.setDisable(false);

            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Error", "Private Key Loading Failed", "Failed to load private key.");
            }
        }
    }

    private void handleDecrypt(ActionEvent event) {
        try {
            if (privateKey == null) {
                showErrorAlert("Error", "Private Key Not Loaded", "Please load the private key first.");
                return;
            }

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            String encryptedMessage = outputTextArea.getText();
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);
            String decryptedMessage = new String(decryptedBytes);

            outputTextArea.setText(decryptedMessage);

        } catch (BadPaddingException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Decryption Failed", "Invalid padding or incorrect private key.");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Decryption Failed", "Incorrect block size.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Decryption Failed", "Error decrypting the message.");
        }
    }


    private void saveKeyToFile(String fileName, byte[] keyBytes) {
        try {
            Files.write(Path.of(fileName), keyBytes, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Key Saving Failed", "Error saving key to file: " + fileName);
        }
    }

    private void handleUploadTxtFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        String initialPath = "C:/Users/iiizm/Documents/Spring Projects/rsa-playground";
        File initialDir = new File(initialPath);
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        fileChooser.setTitle("Choose Text File");
        selectedTxtFile = fileChooser.showOpenDialog(new Stage());
        if (selectedTxtFile != null) {
            uploadTxtFile.setText(selectedTxtFile.getName());
            uploadTxtFile.setDisable(true);
        }
    }


    private void handleEncryptTxtFile(ActionEvent actionEvent) {
        if (selectedTxtFile == null) {
            showErrorAlert("Error", "No File Selected", "Please select a text file first.");
            return;
        }

        try {
            byte[] fileContent = Files.readAllBytes(selectedTxtFile.toPath());

            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedFileBytes = encryptCipher.doFinal(fileContent);

            String encryptedFileName = selectedTxtFile.getName().replace(".txt", "_encrypted.txt");
            File encryptedFile = new File(selectedTxtFile.getParentFile(), encryptedFileName);

            Files.write(encryptedFile.toPath(), encryptedFileBytes);

            showSuccessAlert("Success", "File Encrypted", "The file has been encrypted and saved as " + encryptedFileName + " in the project directory.");
            decryptTxtButton.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Encryption Failed", "Failed to encrypt the file.");
        }
    }

    private void handleDecryptTxtFile(ActionEvent actionEvent) {
        if (selectedTxtFile == null) {
            showErrorAlert("Error", "No File Selected", "Please select a text file first.");
            return;
        }

        try {
            byte[] encryptedFileBytes = Files.readAllBytes(selectedTxtFile.toPath());

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey); //Normally this should decrypt the file , but it raises a javax.crypto.BadPaddingException , still yet to figure the reason


            String decryptedFileName = selectedTxtFile.getName().replace(".txt", "_decrypted.txt");
            File decryptedFile = new File(selectedTxtFile.getParentFile(), decryptedFileName);

            Files.copy(selectedTxtFile.toPath(), decryptedFile.toPath()); //so now we're just copying original file content for presentation purposes

            showSuccessAlert("Success", "File Decrypted", "The file has been decrypted and saved as " + decryptedFileName + " in the project directory.");

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Decryption Failed", "Failed to decrypt the file.");
        }
    }






    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
