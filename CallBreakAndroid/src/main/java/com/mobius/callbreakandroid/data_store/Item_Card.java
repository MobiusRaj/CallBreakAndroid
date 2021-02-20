package com.mobius.callbreakandroid.data_store;


public class Item_Card implements Comparable<Item_Card>, Cloneable {


    int CardValue, CardColor;
    int deckNumber;
    int GroupNumber;
    boolean isValidGroup, isValidSequence;
    int PairNumber;
    String CardColorStr;


    public int getGroupNumber() {
        return GroupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        GroupNumber = groupNumber;
    }

    public int getCardValue() {
        return CardValue;
    }

    public void setCardValue(int cardValue) {
        CardValue = cardValue;
    }

    public int getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(int value) {
        deckNumber = value;
    }

    public int getCardColor() {
        return CardColor;
    }

    public void setCardColor(int cardColor) {
        CardColor = cardColor;
    }

    public boolean getIsValidGroup() {
        return isValidGroup;
    }

    public void setIsValidGroup(boolean b) {
        this.isValidGroup = b;
    }

    public boolean getIsValidSequence() {
        return isValidSequence;
    }

    public void setIsValidSequence(boolean b) {
        this.isValidSequence = b;
    }

    public int getPairNumber() {
        return PairNumber;
    }


    public void setPairNumber(int pairNumber) {
        PairNumber = pairNumber;
    }

    @Override
    public String toString() {


        if (CardColor == 0) {
            CardColorStr = "f";
        } else if (CardColor == 1) {
            CardColorStr = "c";
        } else if (CardColor == 2) {
            CardColorStr = "k";
        } else if (CardColor == 3) {
            CardColorStr = "l";
        } else if (CardColor == 4) {
            CardColorStr = "j";
        }
        return CardColorStr + "-" + CardValue + "-" + deckNumber + " => " + GroupNumber + " => " + isValidGroup;
    }

    public String getCardColorStr() {
        if (CardColor == 0) {
            CardColorStr = "f";
        } else if (CardColor == 1) {
            CardColorStr = "c";
        } else if (CardColor == 2) {
            CardColorStr = "k";
        } else if (CardColor == 3) {
            CardColorStr = "l";
        } else if (CardColor == 4) {
            CardColorStr = "j";
        }
        return CardColorStr;
    }

    @Override
    public int compareTo(Item_Card another) {

        try {
            if (another == null || this == null) {
                return 0;
            }

            int compareResult = Integer.compare(this.CardColor, another.CardColor);
            if (compareResult == 0) {
                compareResult = Integer.compare(this.CardValue, another.CardValue);
            }
            return compareResult;

            /*if (this.CardColor < another.CardColor) {
                return -1;
            } else if (this.CardColor > another.CardColor) {
                return 1;
            } else {
                // suit is identical: compare number
                *//*if (this.CardValue < another.CardValue) {
                    return -1;
                } else if (this.CardValue > another.CardValue) {
                    return 1;
                } else {
                    return 0;
                }*//*
                return Integer.compare(this.CardValue, another.CardValue);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public boolean equals(Object o) {

        Item_Card iCard = (Item_Card) o;

        try {
            return iCard.CardColor == this.CardColor && iCard.CardValue == this.CardValue && iCard.deckNumber == this.deckNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Item_Card clone() {
        Item_Card clone = null;
        try {
            clone = (Item_Card) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
            // won't happen
        }
        return clone;
    }

    public String getCardString() {

        String card = "";
        if (getCardColor() == 0) {
            card = "f";
        } else if (getCardColor() == 1) {
            card = "c";
        } else if (getCardColor() == 3) {
            card = "l";
        } else if (getCardColor() == 2) {
            card = "k";
        } else if (getCardColor() == 4) {
            card = "j";
        }
        card = card + "-" + (getCardValue()) + "-" + (getDeckNumber());
        return card;
    }
}
