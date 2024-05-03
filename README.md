ZScanner is an app for mobile computers with Android (tested on Zebra MC9300).
Basically ZScanner serves as thin client. It communicates with app server via REST API.
App server sends json with screen / fields definition, ZScanner displays that screen and fields, 
gathers user input (you can scan barcodes or enter data manually), send back json with field values
for processing by app server. 


![zscanner drawio](https://github.com/deutor/ZScanner/assets/21099156/2a6427f8-6a55-43a4-929b-885713d1dc18)



Typical usage looks like:
1) App server sends info "create screen "Relocate item", add fill-in named "code" with empty value, add fill-in named "quantity" with empty value,
   add label "Scan code:", add label "Enter quantity"
2) Zscanner creates new screen and waits for entering data, pressing Enter on last field will send data back to app server
-------------------|
|  Relocate item   |
|------------------|
| Scan code:       |
|                  |
| ..............   |
|                  |
| Enter quantity:  |
|                  |
| .........        |
|                  |
\                  /
  \               /

3) App server receives json data similar to:
{"data":[{"name":"goSrc","value":"quantity"},{"name":"code","value":"19001120001239"},{"name":"quantity", "value":"17"],"request":"go","sessionID":"6e70a294-cb7d-11ee-97d2-1a9fa01210e2","userID":"1"}

4) App server processes that data and sends next screen
   "create screen "Select destination", add fill-in named "destination" with empty value, add label "Scan location"
and so on
