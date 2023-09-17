from django.shortcuts import render
from .models.clubs import Clubs
from .models.reviews import Reviews
from .models.musicGenres import MusicGenres
from .models.gpsCoords import GPSCoords
from .models.events import Events
from .models.drinks import Drinks
from .models.order import Order
from .models.orderItem import OrderItem
from .models.imagesReview import ImagesReview
from django.http import JsonResponse
import json
from django.core.serializers.json import DjangoJSONEncoder
import logging
from django.http import HttpResponse
import datetime
from django.conf import settings
from .forms.ReviewsByClubIdForm import ReviewsByClubIdForm
from .forms.ClubById import ClubByIdForm
from .forms.insertReviewForm import InsertReviewForm
from .forms.deleteReviewForm import deteleReviewForm
from .forms.editReviewForm import editReviewForm
from .forms.reviewsByUserIdForm import ReviewsByUserIdForm
from .forms.getTableIdsForm import GetTableIdsForm
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
import base64 
import io
from PIL import Image
from django.core.files.uploadedfile import InMemoryUploadedFile
from django.core.files.base import ContentFile

logger = logging.getLogger("mylogger")
#logger.info("here is the response:" form.cleaned_data['email'] \n")
def drinksSerialize():
    j=[]
    drinks=Drinks.objects.all()
    for drink in drinks:
        j.append(drinkSerialize(drink))
    j=json.dumps(j,cls=DjangoJSONEncoder)
    return j

def drinkSerialize(drink):
    data={}
    data["name"]=drink.name
    data["ingredients"]=drink.ingredients
    #controlla nell'app se ci sta ancora la stringa
    data["imagePath"]=settings.SERVER_DOMAIN+drink.imgUrl.url
    return data
    
def eventsSerialize():
    j=[]
    events=Events.objects.all()
    for event in events:
        j.append(eventSerialize(event))
    j=json.dumps(j,cls=DjangoJSONEncoder)
    return j
    
def eventSerialize(event):
    data={}
    data["id"]=event.id
    data["clubId"]=event.club.id
    data["date"]=event.date.strftime( '%b %d, %Y %I:%M:%S %p')
    data["description"]=event.description
    data["imgUrl"]=event.imgUrl
    data["musicGenres"]=musicGeneresSerializeByEventId(event.id)
    data["name"]=event.name
    return data
    
    
def clubsSerialize():
    j=[]
    clubs=Clubs.objects.all()
    for club in clubs:
        j.append(clubSerialize(club))
    j=json.dumps(j,cls=DjangoJSONEncoder)
    return j
    
def clubSerialize(club):
    data={}
    data["id"]=club.id
    data["name"]=club.name
    data["address"]=club.address
    data["description"]=club.description
    data["imgUrl"]=settings.SERVER_DOMAIN+club.image.url
    data["locationType"]=club.locationType
    data["simpleTicketPrice"]=club.simpleTicketPrice
    data["tableTicketPrice"]=club.tableTicketPrice
    data["rating"]=club.rating
    data["musicGenres"]= musicGenresSerializeByClubId(club.id)
    data["gpsCords"]= gpsCoordsSerialize(club.id)
    #data["reviews"]=reviewsSerializeByClub(club.id)
    return data

def reviewsSerializeByUser(user_id):
    data=[]
    user=User.objects.get(id=user_id)
    reviews=user.reviews_set.all()
    
    for review in reviews:
        data.append(reviewSerialize(review))
    
    return data

def reviewsSerializeByClub(club_id):
    data=[]
    club=Clubs.objects.get(id=club_id)
    reviews=club.reviews_set.all()
    
    for review in reviews:
        data.append(reviewSerialize(review))
    
    return data
    

def reviewSerialize(review):
    #check for images settings.SERVER_DOMAIN+review.images.
    img=[]
    for i in review.imagesreview_set.filter().exclude(image=None).all():
        #image url ha gia lo / all'inizio
        img.append(settings.SERVER_DOMAIN+i.image.url)
    """
    if review.imagesreview_set:
        img=[]
        img.append(settings.SERVER_DOMAIN+review.images.url)
    else:
        img=[]
    """
    data={}
    data["id"]=review.id
    data["date"]=review.date.strftime('%d/%m/%Y') 
    data["description"]=review.description
    data["images"]=img
    data["rating"]=review.rating
    data["user"]=userSerialize(review.user)
    data["clubId"]=review.club.id
    #data["clubName"]=clubname
    return data
 
#per il momento ometto l'username   
def userSerialize(user):
    data={}
    data["id"]=user.id
    data["name"]=user.first_name
    data["surname"]=user.last_name
    return data

def musicGeneresSerializeByEventId(eventID):
    l=[]
    event=Events.objects.filter(id=eventID)
    if (event.exists()):
        musicGenres=event.first().MusicGenres.all()
        for musicGenre in musicGenres:
            l.append(musicGenre.type)

    return l

def musicGenresSerializeByClubId(clubID):
    l=[]
    club=Clubs.objects.filter(id=clubID)
    if (club.exists()):
        musicGenres=club.first().MusicGenres.all()
        for musicGenre in musicGenres:
            l.append(musicGenre.type)

    return l

def gpsCoordsSerialize(clubID):
    l=[]
    club=Clubs.objects.filter(id=clubID)
    if club.exists():
        coords=club.first().gpsCoords
        l.append(coords.x)
        l.append(coords.y)
    return l

def getClubs(request):
    if request.method=='GET':
        
        return  HttpResponse(clubsSerialize(), content_type="application/json")

def getEvents(request):
    if request.method=='GET':
        
        return  HttpResponse(eventsSerialize(), content_type="application/json")
    
def getDrinks(request):
    if request.method=='GET':
        
        return  HttpResponse(drinksSerialize(), content_type="application/json")
    
def getClubByIdRequest(request):
    if request.method=='POST':
        form=ClubByIdForm(request.POST)
        if form.is_valid():
            club =Clubs.objects.get(id=form.cleaned_data["id"])
            if club is not None:
                return  HttpResponse(json.dumps( clubSerialize(club), cls=DjangoJSONEncoder),
                                            content_type="application/json")
            else:
                
                return HttpResponse(json.dumps( {}, cls=DjangoJSONEncoder),
                                            content_type="application/json")

def getReviewsByClubIdRequest(request):
    if request.method=='POST':
        form=ReviewsByClubIdForm(request.POST)
        if form.is_valid():
            return  HttpResponse(json.dumps(reviewsSerializeByClub(form.cleaned_data["id"]),cls=DjangoJSONEncoder),
                                            content_type="application/json")
        else:
            response_data={}
            response_data["success"]=False
            response_data["message"]=dict(form.errors.items())
            return HttpResponse(json.dumps(response_data), content_type="application/json")     

def getReviewsByUserIdRequest(request):
    if request.method=='POST':
        form=ReviewsByUserIdForm(request.POST)
        if form.is_valid():
            return  HttpResponse(json.dumps(reviewsSerializeByUser(form.cleaned_data["id"]),cls=DjangoJSONEncoder),
                                            content_type="application/json")
        else:
            response_data={}
            response_data["success"]=False
            response_data["message"]=dict(form.errors.items())
            return HttpResponse(json.dumps(response_data), content_type="application/json")   

def insertReviewRequest(request):
    
    if request.method=='POST':
        
        form=InsertReviewForm(request.POST)
        if form.is_valid():
            date=form.cleaned_data["date"].strftime('%Y-%m-%d')
            review = Reviews.objects.create(
                user=User.objects.get(id=form.cleaned_data["userId"]),
                club=Clubs.objects.get(id=form.cleaned_data["clubId"]),
                date= date,
                description =form.cleaned_data["description"],
                rating = form.cleaned_data["rating"],
                images=form.cleaned_data["images"]
                
            )
            review.save()
            created = review.id
            
            if not created:
                response_data={}
                response_data["success"]=False
                response_data["id"]=-1
                response_data["message"]="c'è stato un errore con l'inserimento"
            else:
                response_data={}
                response_data["success"]=True
                response_data["id"]=created
                response_data["message"]="recensione aggiunta correttamente!"
                
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        else:
            response_data={}
            response_data["success"]=False
            response_data["message"]=dict(form.errors.items())
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        
def decodeDesignImage(data):
    try:
        data = base64.b64decode(data)
        buf = io.BytesIO(data)
        img = Image.open(buf)
        return img
    except:
        return None

def insertReviewRequest2(request):
    if request.method=='POST':
        try:
            o=json.loads(request.body)
            date=datetime.datetime.strptime(o["date"], '%d/%m/%Y').strftime('%Y-%m-%d') 
            review = Reviews.objects.create(
                user=User.objects.get(id=o["userId"]),
                club=Clubs.objects.get(id=o["clubId"]),
                date= date,
                description =o["description"],
                rating =o["rating"],
                #images=imgs[0]
            )
            review.save()
                        
            index=0
            for codedImage in o["images"]:
                imageReview=ImagesReview.objects.create(review=review)
                token="review_"+str(review.id)+"_"+str(index)+".jpg"
                decodedImage=base64.b64decode(codedImage)
                file=ContentFile(decodedImage,token)
                imageReview.image.save(token, file, save=True)
                imageReview.save()
                index+=1
            
            created = review.id
            
            if not Reviews.objects.filter(id=created).exists():
                response_data={}
                response_data["success"]=False
                response_data["id"]=-1
                response_data["message"]="c'è stato un errore con l'inserimento"
            else:
                
                #Ricomputo l'average
                club=Clubs.objects.get(id=o["clubId"])
                avg=0.0
                counter=0
                for review in club.reviews_set.all():
                    avg+=review.rating
                    counter+=1
                club.rating=avg/counter
                club.save()
                    
                response_data={}
                response_data["success"]=True
                response_data["id"]=created
                response_data["message"]="recensione aggiunta correttamente!"
                
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        
        except Exception as e:
            
            logger.info(e)
            response_data={}
            response_data["success"]=False
            response_data["message"]="problema nella creazione"
            return HttpResponse(json.dumps(response_data), content_type="application/json")


def deleteReviewRequest(request):
    if request.method=='POST':
        form=deteleReviewForm(request.POST)
        if form.is_valid():
            review = Reviews.objects.filter(id=form.cleaned_data["id"])
            
            if (review.exists()):
                review=review.first()
                review.delete()
                response_data={}
                response_data["success"]=True
                response_data["message"]="recensione rimossa correttamente!"
            else:
                response_data={}
                response_data["success"]=False
                response_data["message"]="c'è stato un errore con l'inserimento"
           
                
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        else:
            response_data={}
            response_data["success"]=False
            response_data["message"]=dict(form.errors.items())
            return HttpResponse(json.dumps(response_data), content_type="application/json")

def editReviewRequest(request):
    if request.method=='POST':
        form=editReviewForm(request.POST)
        if form.is_valid():
            review=Reviews.objects.filter(id=form.cleaned_data["id"])
            if review.exists():
                review=review.first()
                review.description=form.cleaned_data["description"]
                review.rating=form.cleaned_data["rating"]
                review.images=form.cleaned_data["images"]
                review.save()

                response_data={}
                response_data["success"]=True
                response_data["message"]="recensione modificata correttamente!"
            else:
                response_data={}
                response_data["success"]=False
                response_data["message"]="recensione non presente!"
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        
        else:
            response_data={}
            response_data["success"]=False
            response_data["message"]=dict(form.errors.items())
            return HttpResponse(json.dumps(response_data), content_type="application/json")

def editReviewRequest2(request):
    
    try:
        o=json.loads(request.body)
        review=Reviews.objects.filter(id=o["id"])
        if review.exists():
            review=review.first()
            
            for i in review.imagesreview_set.all():
                i.delete()
                
            index=0
            for codedImage in o["images"]:
                imageReview=ImagesReview.objects.create(review=review)
                token="review_"+str(review.id)+"_"+str(index)+".jpg"
                decodedImage=base64.b64decode(codedImage)
                file=ContentFile(decodedImage,token)
                imageReview.image.save(token, file, save=True)
                imageReview.save()
                index+=1
                
            review.description=o["description"]
            review.rating=o["rating"]
            review.save()
            
            #Ricomputo l'average
            club=review.club
            avg=0.0
            counter=0
            for review in club.reviews_set.all():
                avg+=review.rating
                counter+=1
            club.rating=avg/counter
            club.save()
            
            response_data={}
            response_data["success"]=True
            response_data["message"]="recensione modificata correttamente!"
        else:
            response_data={}
            response_data["success"]=False
            response_data["message"]="recensione non presente!"
            
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    
    except Exception as e:
        logger.info(e)
        response_data={}
        response_data["success"]=False
        response_data["message"]="errore"
        return HttpResponse(json.dumps(response_data), content_type="application/json")
        

def insertOrder(request):
    if request.method=='POST':
        o=json.loads(request.body)
        
        try:
        
            logger.info(o["date"])
            if o["date"]=="":
                order=Order.objects.create(
                user=User.objects.get(id=o["userId"]),
                club=Clubs.objects.get(id=o["clubId"]),
                createdAt=o["createdAt"],
                tableIdsList=o["tableIdsList"],
                tableIds=o["tableIds"]
            )
            else:
                order=Order.objects.create(
                user=User.objects.get(id=o["userId"]),
                club=Clubs.objects.get(id=o["clubId"]),
                createdAt=o["createdAt"],
                date=o["date"],
                tableIdsList=o["tableIdsList"],
                tableIds=o["tableIds"]
            )
                
            
            order.save()
            
            drinks=json.loads(o["drinks"])
            for item in drinks:
                orderItem=OrderItem.objects.create(
                    name=item["name"],
                    quantity=item["quantity"],
                    unitaryPrice=item["unitaryPrice"],
                    type=item["type"],
                    order=order
                )
                orderItem.save()
                
            tickets=json.loads(o["tickets"])
            for item in tickets:
                orderItem=OrderItem.objects.create(
                    name=item["name"],
                    quantity=item["quantity"],
                    unitaryPrice=item["unitaryPrice"],
                    type=item["type"],
                    order=order
                )
                orderItem.save()
                
            response_data={}
            response_data["success"]=True
            response_data["message"]="Correttamente registrato"
            
            logger.info("ok!")
            return HttpResponse(json.dumps(response_data), content_type="application/json")
    
        except Exception as e:
            #da provare ma tanto leva in cascata!
            #order.delete()
            logger.info("NO ok!")
            logger.info(e)
            response_data={}
            response_data["success"]=False
            response_data["message"]="problema nell'inserimento"
            return HttpResponse(json.dumps(response_data), content_type="application/json")

def serializeOrderItem(orderItem):
    item={}
    item["id"]=orderItem.id
    item["name"]=orderItem.name
    item["quantity"]=orderItem.quantity
    item["unitaryPrice"]=orderItem.unitaryPrice
    item["type"]=orderItem.type
    item["orderId"]=orderItem.order.id
    return item
    

def getOrdersByUserId(request):
    try:
        if request.method=='POST':
                form=ReviewsByUserIdForm(request.POST)
                if form.is_valid():
                    user=User.objects.get(id=form.cleaned_data["id"])
                    orders=Order.objects.filter(user=user)
                    output=[]
                    for order in orders:
                        o={}
                        o["id"]=order.id
                        o["userId"]=order.user.id
                        o["clubId"]=order.club.id
                        o["createdAt"]=order.createdAt
                        o["date"]=str(order.date)
                        o["tableIds"]=order.tableIds
                        o["tableIdsList"]=order.tableIdsList
                        o["drinks"]=[]
                        for item in order.orderitem_set.filter(type="Drink"):
                            o["drinks"].append(serializeOrderItem(item))
                        o["tickets"]=[]
                        for item in order.orderitem_set.filter(type="Ticket"):
                            o["tickets"].append(serializeOrderItem(item))
                            
                        output.append(o)
        logger.info("ordini inviati!")
        response_data={}
        response_data["success"]=True
        response_data["message"]="tutto ok"
        return HttpResponse(json.dumps(output, cls=DjangoJSONEncoder) ,content_type="application/json")
    
    except Exception as e:
            logger.info("NO ordini inviati!")
            logger.info(e)
            response_data={}
            response_data["success"]=False
            response_data["message"]="problema nel prendere i dati"
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        
def getTableIds(request):
    
    try:
        if request.method=='POST':
            form=GetTableIdsForm(request.POST)
            if form.is_valid():
                date=form.cleaned_data["date"]
                club=Clubs.objects.get(id=form.cleaned_data["id"])
                
                j=[]
                #j['tableIds']=[]
                orders=Order.objects.filter(club=club,date=date)
                if orders.exists():
                    for order in orders:
                        for id in order.tableIdsList:
                            j.append(id)
                            
                return HttpResponse(json.dumps(j, cls=DjangoJSONEncoder) ,content_type="application/json")
                
    except Exception as e:
        logger.info("NO Ids inviati!")
        logger.info(e)
        response_data={}
        response_data["success"]=False
        response_data["message"]="problema nel prendere i dati"
        return HttpResponse(json.dumps(response_data), content_type="application/json")