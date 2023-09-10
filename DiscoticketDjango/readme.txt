
#installed package
Pillow (for images)
Django
django-multiupload
django-cleanup

##CREATE DUMPS OF TABLES
python .\manage.py dumpdata auth.user --indent 4 > users.json
python .\manage.py dumpdata AccountsManager.EmailConfirmationCode --indent 4 > emailConfirmationCode.json

##INITIALIZE DUMP OF MODELS
python .\manage.py loaddata DiscoticketDB/fixtures/clubs.json
iniziando da musicGenres, gpsCoords, clubs , reviews!

#ENTER IN THE SHELL
python ./manage shell
#TEST MODEL IN THE SHELL
from DiscoticketDB.models.clubs import *
from django.contrib.auth.models import User

#USERS PASSWORDS
Vafammoc
il pk degli utenti si prende da x1 e si aggiunge 2 quindi (1+2=3)
di x2 è 4....
#ADMIN
password=Ciao
user=Nicolas

#GET IMG LINK
settings.SERVER_DOMAIN + imgObject.url

#SCRIPT PER CONVERTIRE DA JSON LUCA
json-convert/converter.py
#NON RISCRIVE MUSIC GENRES!!!!