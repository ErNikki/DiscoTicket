from django.urls import path

from . import views

urlpatterns = [
   path("getClubs", views.getClubs, name="getClubs"),
   path("getEvents", views.getEvents, name="getEvents"),
   path("getDrinks", views.getDrinks, name="getDrinks"),
   path("getReviewsByClubId", views.getReviewsByClubIdRequest),
   path("getReviewsByUserId", views.getReviewsByUserIdRequest),
   path("getClubById",views.getClubByIdRequest),
   path("insertReview",views.insertReviewRequest),
   path("deleteReview",views.deleteReviewRequest),
   path("editReview",views.editReviewRequest),
   path("insertOrder",views.insertOrder),
   path("getOrdersByUserId",views.getOrdersByUserId),
   path("getTableIds",views.getTableIds),
   path("insertReview2",views.insertReviewRequest2),
   
]