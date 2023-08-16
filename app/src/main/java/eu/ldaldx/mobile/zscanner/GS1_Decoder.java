package eu.ldaldx.mobile.zscanner;

import java.util.HashMap;

public class GS1_Decoder {
    static final char ctrlFNC1 = '\u00e8';
    static final char ctrlGS = '\u001d';
    private String rawData;

    private HashMap<String, String> gs1Data;
    GS1_Decoder() {
            rawData = "";
            gs1Data = new HashMap<>();
    }

    public boolean containsKey(String aiKey) {
        return gs1Data.containsKey(aiKey);
    }

    public String getRawData() {
        return rawData;
    }

    public String getValueForAI(String aiCode) {
        if(gs1Data.containsKey(aiCode)) {
            return gs1Data.get(aiCode);
        }

        return "";
    }

    public int decode(String rawData) {
        this.rawData = rawData;
        gs1Data.clear();

        String prepToSplit = rawData.replace(ctrlFNC1, ctrlGS);
        String[] lstElem = prepToSplit.split( String.valueOf(ctrlGS));
        String aiCode;
        String value;
        for(int i = 0; i< lstElem.length;i++) {
            if(lstElem[i].length() < 2) continue;
            int aiCounter = 0;

            // process code and try split it into GS1 AI values
            do {
/*
    AI	Description	Format	Short Name
    00	Serial Shipping Container Code	n2+n18	SSCC
    01	Global Trade Item Number	n2+n14	GTIN
    02	GTIN of trade items contained in a logistic unit	n2+n14	CONTENT
    10	Batch or lot number	n2+an..20	BATCH/LOT
    11	Production date (YYMMDD)	n2+n6	PROD DATE
    15	Best before date (YYMMDD)	n2+n6	BEST BEFORE or BEST BY
    17	Expiration date (YYMMDD)	n2+n6	USE BY or EXPIRY
    37	Count of trade items contained in a logistic unit	n2+n..8	COUNT
    91-99	Company internal information	n2+an..90	INTERNAL
    402	Global Shipment Identification Number	n3+n17	GSIN
 */
                int ai;

                try {
                    aiCode = lstElem[i].substring(0, 2); // end index is exclusive
                    ai = Integer.parseInt(aiCode);


                    if (ai >= 40 && ai<= 49)  {
                        aiCode = lstElem[i].substring(0, 3); // end index is exclusive
                        ai = Integer.parseInt(aiCode);
                    }
                }
                catch (NumberFormatException ex) {
                        gs1Data.clear();
                        break; //apparently code is not a valid GS1, so need to clear all decoded parts so far - either code is correct or it is just some random string looking like GS1 data
                    }

                switch(ai) {
                    case 0:
                        value = lstElem[i].substring(2, 20); // 18 chars
                        break;
                    case 1:
                    case 2:
                        value = lstElem[i].substring(2, 16); // GTIN - 14 chars
                        break;
                    case 11:
                    case 15:
                    case 17:
                        value = lstElem[i].substring(2, 8); // date - 6 chars
                        break;
                    case 37:
                        value = lstElem[i].substring(2, lstElem[i].length()); // Count of trade items or trade item pieces contained in a logistic unit
                        break;
                    case 402:
                        value = lstElem[i].substring(3, 20); // GSIN - 17 chars
                        break;
                    case 412:
                        value = lstElem[i].substring(3, 16); // GLN - 13 chars
                        break;
                    case 10: // batch or lot number
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    case 97:
                    case 98:
                    case 99:
                        value = lstElem[i].substring(2); // variable length code is separated from other codes, so no more codes in this string
                        break;
                    default:
                        value = "";
                        break;
                } // switch

                if(value.length() > 0) {
                    gs1Data.put(aiCode, value);
                }
                else {
                    // code was unrecognized - so we do not know whether it was variable or fixed one, further processing would give just random results
                    break;

                }

                lstElem[i] = lstElem[i].substring( value.length() + aiCode.length());
                if(lstElem[i].length() < 1) break;
                //process max 10 codes - only 1D usually has 1-4 elements, 2D can store more, but our case has only 9 common codes + 9x company internal
                aiCounter++;
            } while(aiCounter <= 10);
        }
        return gs1Data.size();
    }

    public String getAIs() {
        StringBuilder values = new StringBuilder();

        for(String key : gs1Data.keySet()) {
            values.append("\n").append(key).append(" : ").append(gs1Data.get(key));
        }

        return values.toString();
    }
} // class
