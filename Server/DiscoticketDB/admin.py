from django.contrib import admin
from .models.gpsCoords import GPSCoords
from .models.musicGenres import MusicGenres
from .models.clubs import Clubs
from .models.reviews import Reviews
from .models.drinks import Drinks
from .models.events import Events
from .models.orderItem import OrderItem
from .models.order import Order
from .models.imagesReview import ImagesReview

# Register your models here.
admin.site.register(GPSCoords)
admin.site.register(MusicGenres)
admin.site.register(Clubs)
admin.site.register(Drinks)
admin.site.register(Reviews)
admin.site.register(Events)
admin.site.register(Order)
admin.site.register(OrderItem)
admin.site.register(ImagesReview)
