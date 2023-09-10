import json
import random
import datetime

min_users=2
max_users=8

json_clubs= open("clubs.json")
input_data_clubs=json.load(json_clubs)

clubs_file=open("../DiscoTicketDB/fixtures/clubs.json","w")
gps_file=open("../DiscoTicketDB/fixtures/gpsCoords.json","w")
reviews_file=open("../DiscoTicketDB/fixtures/reviews.json","w")

club_data=[]
gps_data=[]
reviews_data=[]




for club in input_data_clubs:
    
    #CLUBS GPS AND REVIEWS
    c={}
    c["model"]= "DiscoticketDB.clubs"
    cFields={} 
    cFields["id"]=club["id"]
    cFields["address"]=club["address"]
    cFields["description"]=club["description"]
    cFields["imgUrl"]=club["imgUrl"]
    cFields["locationType"]=club["locationType"]
    cFields["MusicGenres"]=club["musicGenres"]
    cFields["gpsCoords"]=club["id"]
    cFields["name"]=club["name"] 
    cFields["simpleTicketPrice"]=club["simpleTicketPrice"]
    cFields["tableTicketPrice"]=club["tableTicketPrice"]
    #cFields["reviews"]=[]
    
    gpsCoord={}
    gpsCoord["model"]= "DiscoticketDB.gpsCoords"
    gpsCoord["pk"]=club["id"]
    gpsFields={}
    gpsFields["x"]=club["gpsCords"][0]
    gpsFields["y"]=club["gpsCords"][1]
    gpsCoord["fields"]=gpsFields
    gps_data.append(gpsCoord)
    
    ratingCount=0
    reviewsCount=0
    for review in club["reviews"]:
        r={}
        r["model"]="DiscoticketDB.reviews"
        rFields={}
        rFields["club"]=club["id"]
        rFields["date"]=datetime.datetime.strptime(review["date"], '%d/%m/%Y').strftime('%Y-%m-%d') 
        rFields["description"]=review["description"]
        rFields["images"]=[]
        rFields["rating"]=review["rating"]
        rFields["user"]=random.randint(min_users,max_users)
        ratingCount+=review["rating"]
        reviewsCount+=1
        r["fields"]=rFields
        reviews_data.append(r)
    
    cFields["rating"]=ratingCount/reviewsCount
    c["fields"]=cFields
    club_data.append(c)



#DRINKS
json_drinks= open("drinks.json")
input_data_drinks=json.load(json_drinks)

drinks_file=open("../DiscoTicketDB/fixtures/drinks.json","w")

drinks_data=[]
pk=1
for drink in input_data_drinks:
    d={}
    d["model"]="DiscoticketDB.Drinks"
    d["pk"]=pk
    dFields={}
    dFields["name"]=drink["name"]
    dFields["ingredients"]=drink["ingredients"]
    dFields["imgUrl"]="images/drinks/"+drink["imagePath"]
    d["fields"]=dFields
    drinks_data.append(d)
    pk+=1
    
    
#EVENTS
json_events= open("events.json")
input_data_events=json.load(json_events)

events_file=open("../DiscoTicketDB/fixtures/events.json","w")

events_data=[]

for event in input_data_events:
    e={}
    e["model"]="DiscoticketDB.Events"
    e["pk"]=event["id"]
    eFields={}
    eFields["club"]=event["clubId"]
    eFields["date"]=datetime.datetime.strptime(event["date"], '%b %d, %Y %I:%M:%S %p').strftime('%Y-%m-%d %H:%M:%S') 
    eFields["description"]=event["description"]
    eFields["imgUrl"]=event["imgUrl"]
    eFields["MusicGenres"]=event["musicGenres"]
    eFields["name"]=event["name"]
    e["fields"]=eFields
    events_data.append(e)
    
    
json.dump(reviews_data,reviews_file,indent=4)
json.dump(club_data,clubs_file,indent=4)
json.dump(gps_data,gps_file,indent=4)
json.dump(drinks_data,drinks_file,indent=4)
json.dump(events_data,events_file,indent=4)
 

json_clubs.close()
clubs_file.close()
gps_file.close()
reviews_file.close()
drinks_file.close()
events_file.close()
    

    