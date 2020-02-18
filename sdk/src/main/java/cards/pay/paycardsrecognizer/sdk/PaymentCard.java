package cards.pay.paycardsrecognizer.sdk;

public class PaymentCard {
    private char[] cardNumber;
    private String expirationDate;
    private String cardHolder;
    private byte[] cardImage;

    public PaymentCard() {
        cardNumber = null;
        expirationDate = "";
        cardHolder = "";
        cardImage = new byte[0];
    }

    public PaymentCard(char[] cardNumber, String expirationDate, String cardHolder, byte[] cardImage) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardHolder = cardHolder;
        this.cardImage = cardImage;
    }

    public char[] getCardNumber() {
        return cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public byte[] getCardImage() {
        return cardImage;
    }

    public void clearData() {
        clearCardNumber();
        clearCardPhoto();
        expirationDate = "";
        cardHolder = "";
    }

    private void clearCardNumber() {
        if (cardNumber == null) {
            return;
        }
        for (int i = 0; i < cardNumber.length; i++) {
            cardNumber[i] = '\0';
        }
        cardNumber = null;
    }

    private void clearCardPhoto() {
        if (cardImage == null) {
            return;
        }
        for (int i = 0; i < cardImage.length; i++) {
            cardImage[i] = (byte) '\0';
        }
        cardImage = null;
    }
}
