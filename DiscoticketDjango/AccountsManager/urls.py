from django.urls import path

from .views import views

urlpatterns = [
    path("", views.index, name="index"),
    path("registration", views.registrationRequest, name="registration"),
    path('emailConfirmation/<str:username>/<str:emailConfirmationCode>', views.emailConfirmationManager),
    path('ReSendConfirmationEmail',views.REsendConfirmationEmail, name="ReSendConfirmationEmail"),
    path('forgotPassword',views.forgotPassword, name="forgotPassword"),
    path('resetPassword/<str:username>/<str:resetPasswordCode>',views.forgotPasswordManager, name='resetPassword'),
    path('login',views.myLogin, name='login'),
    path('logout',views.logOut, name='logout'),    
    path('deleteAccount',views.deleteAccountRequest, name='deleteAccount'),
    path('changePassword',views.changePassword, name='changePassword'),
    path('isSessionActive',views.isSessionActiveRequest,name='isSessionActive'),
    path('getLoggedUser',views.getLoggedUser, name="getLoggedUser"),
]

