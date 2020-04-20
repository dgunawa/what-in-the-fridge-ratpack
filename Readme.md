# Task 1 - What's in the fridge?

## Build
    mvn clean package
    
## Run the fatjar
    java -jar target/what-in-the-fridge-1.0-SNAPSHOT-jar-with-dependencies.jar
    
## Send requests (use curl or httpie):

### login to get token
    http -f POST localhost:5050/login username=foo password=bar
### view what's in refrigerator-1
    http -f GET localhost:5050/refrigerator/1 token:eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.MpgDqS9vRPbyaZlRYrjAeA2SG7QFDl6JdazCoSXWP7U1Gw0cLhpGvg.RoJEAgMTI_VTK7urTpmAzA.OIIOfbpdwZQ5NxG-fWOEC9FlaPpD3tadcFOwrWYyg1bn78VHBf6unaQjinLwNKWL.a_hy7PA8Wi-5w1nno5ExNg
### add some items to refrigerator-1
    http -f POST localhost:5050/refrigerator/1 token:eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.MpgDqS9vRPbyaZlRYrjAeA2SG7QFDl6JdazCoSXWP7U1Gw0cLhpGvg.RoJEAgMTI_VTK7urTpmAzA.OIIOfbpdwZQ5NxG-fWOEC9FlaPpD3tadcFOwrWYyg1bn78VHBf6unaQjinLwNKWL.a_hy7PA8Wi-5w1nno5ExNg  < test/resources/items.json
    
## To get complete functionalities, please refer to ApplicationTest