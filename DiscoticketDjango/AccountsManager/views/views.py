from django.shortcuts import render
from django.core.mail import send_mail
from django.conf import settings

# Create your views here.
from django.http import HttpResponse, JsonResponse
from ..models.Profile import Profile
from ..models.emailConfirmationCode import EmailConfirmationCode
from ..models.resetPasswordCode import ResetPasswordCode
from ..forms.RegistrationForm import RegistrationForm
from ..forms.MailForm import MailForm
from ..forms.PasswordForm import PasswordForm
from ..forms.LoginForm import LoginForm
from ..forms.DeleteAccountForm import DeleteAccountForm
from ..forms.ChangePasswordForm import ChangePasswordForm
from ..forms.GetUserByIdForm import GetUserByIdForm
from ..utils import generateRandomString, createUserFromProfile, updateUserFromProfile
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from . import errors_response
import json
import logging
import threading
import time
from django.contrib.auth import SESSION_KEY
from django.contrib.sessions.models import Session
from http.cookies import SimpleCookie
logger = logging.getLogger("mylogger")
#logger.info("here is the response:" form.cleaned_data['email'] \n")




#HOMEPAGE!
def index(request):
    return HttpResponse("Hello, world. You're at the polls index.")

##
#OTTIMIZZAZIONI:
#0 DA RIVEDERE LA GESTIONE DEGLI ERRORI!!
#1 mettere a blank i code dopo averli utilizzati!
#2 change email while logged
#3 change password while logged
#4 non so se funziona il login dopo la confirmation email
#5 dopo il change password si viene disconnessi!
#6 in forgot password tocca mandare il nome dell'user
#7 in forgot password non so se va bene threding.timer....
##
#REGISTRAZIONE
#PRENDI I DATI TRAMITE CHIAMATA POST
#VERIFICA TRAMITE FORM CHE I DATI SIA OK!
#ASSOCIA ALL'USER UN CONFIRMATION CODE
#INVIA L'EMAIL DI CONFERMA CON IL LINK CON sendEmail()
#Request: username, email, password
def registrationRequest(request):
    if request.method == 'POST':
        form = RegistrationForm(request.POST)
        
        #CHECK SE USERNAME O EMAIL SONO GIà STATE USATE NEL SISTEMA
        if form.is_valid():
            
            username=form.cleaned_data['username']
            email = form.cleaned_data['email']
            password = form.cleaned_data['password']
            name= form.cleaned_data['name']
            surname=form.cleaned_data['surname']
            
            #Creo utente
            user = User.objects.create_user(username,
                                            email,
                                            password,
                                            first_name=name, 
                                            last_name=surname)
            user.save()
            
            #PROVA A CREARE UN emailConfCode FINO A QUANDO NON VIENE INSERITO NEL DB
            #IL CODE è UNICO MA VIENE CREATO RANDOM QUINDI NON è DETTO CHE NON SIA GIà STATO CREATO
            while True:
                code=generateRandomString(64)
                emailConfirmationCode=EmailConfirmationCode(user=user,code=code)
                emailConfirmationCode.save()
                if EmailConfirmationCode.objects.filter(code=code).exists():
                    break
        
            
            response_data = {}
            if(sendConfirmationEmail(user)):
                response_data['success'] = True
                response_data['message'] = "Email sent"
            else:
                response_data['success'] = False
                response_data['message'] = "Cannot send the email"
            return HttpResponse(json.dumps(response_data), content_type="application/json")
            
        else:
            return JsonResponse({
                'success':False,
                "message":dict(form.errors.items())
            })
    else:
        form=RegistrationForm()
        return render(request, "AccountsManager/registration.html", {"form": form})

   # elif request.method == 'GET':
   #     form = SignUpForm()
        
   #return render(request, 'HoloMusic/forms/sign-up-form.html', {'form': form, 'action':settings.SERVER_DOMAIN + 'signUp'})
   
#GENERA LINK DI CONFERMA'AccountsManager/emailConfirmation/'+ email + '/' +code
#INVIA TRAMITE MAIL    
def sendConfirmationEmail(user):
    try:
        f = open(settings.STR_BASE_DIR + '/AccountsManager/text/RegistrationEmail.txt','r')
        code=EmailConfirmationCode.objects.filter(user=user).first().code
        username=user.username
        message = f.read().format(username=username, link=settings.SERVER_DOMAIN + '/AccountsManager/emailConfirmation/'+ username + '/' +code)
        f.close()
        
        subject = 'Welcome to DiscoTicket!'
        email_from = settings.EMAIL_HOST_USER
        email=user.email
        recipient_list = [email,]
       
        
        send_mail(subject, message, email_from, recipient_list, fail_silently=False)
        return True
    except Exception as e:
        return False
    
#invia di nuovo l'email di conferma se l'email è presente nel db e il flag è false(non confermato)
def REsendConfirmationEmail(request):
    if request.method == 'POST':
        form=MailForm(request.POST)
        
        if form.is_valid():
            
            email=form.cleaned_data['email']
            user=User.objects.get(email=email)
            emailCodeIstance = EmailConfirmationCode.objects.filter(user=user)
            
            #se l'email è presente e la flag è ancora false
            if  emailCodeIstance.exists() and not emailCodeIstance.first().flag:
                flag=sendConfirmationEmail(User.objects.get(email=email))
            else:
                flag=False
            
            response_data = {}
            if(flag):
                response_data['success'] = True
                response_data['message'] = "Email sent"
            else:
                response_data['success'] = False
                response_data['message'] = "Cannot send the email"
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        else:
             return JsonResponse({
                'success':False,
                "message":dict(form.errors.items())
            })
    else:
        form=MailForm()
        return render(request, '/AccountsManager/ReSendConfirmationEmail.html' , {"form": form})
    
                
#GESTISTE LINK DI CONFERMA
#Imposta il flag di conferma a true!
def emailConfirmationManager(request, username, emailConfirmationCode):
    
    user=User.objects.get(username=username)
    if EmailConfirmationCode.objects.filter(user=user).filter(code=emailConfirmationCode).exists(): 
        try:
            #user = createUserFromProfile(s[0])
            emailCodeIstance=EmailConfirmationCode.objects.filter(user=user).filter(code=emailConfirmationCode).first()
            emailCodeIstance.flag=True
            emailCodeIstance.save()
            login(request, user, backend='django.contrib.auth.backends.ModelBackend')
            return HttpResponse("your email is confirmed!")
            #return HttpResponseRedirect(settings.SERVER_DOMAIN)
        except:
            return errors_response.server_error(request)
    return errors_response.bad_request(request,None)
             
def forgotPassword(request):
    if request.method=='POST':
        form=MailForm(request.POST)
        
        if form.is_valid():
            
            email=form.cleaned_data['email']
            user=User.objects.filter(email=email)
            if user.exists():
                user=user.first()
                
                resetPassIstance=ResetPasswordCode.objects.filter(user=user)
                if(resetPassIstance.exists()):
                    resetPassIstance.first().delete()
                
                while True:
                    code=generateRandomString(64)
                    forgotPasswordCode=ResetPasswordCode(user=user,code=code)
                    forgotPasswordCode.save()
                    if ResetPasswordCode.objects.filter(code=code).exists():
                        break
                #sendForgotPasswordMail(user)
                #return HttpResponse("If your email is in the database you will receive an email with the link to reset the password")
                response_data={}
                flag=sendForgotPasswordMail(user)
                if(flag):
                    #forse da eliminare
                    threading.Timer(600,removeResetPassCode,[user]).start()
                    response_data['success'] = True
                    response_data['message'] = "Email sent"
                else:
                    response_data['success'] = False
                    response_data['message'] = "Cannot send the email"
                return HttpResponse(json.dumps(response_data), content_type="application/json")
                    #da sistemare
        else:
            response_data['success'] = False
            response_data['message'] = dict(form.errors.items())
            return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        form=MailForm()
        return render(request, "/AccountsManager/ForgotPassword.html", {"form": form})
                    
def sendForgotPasswordMail(user):
    try:
        f = open(settings.STR_BASE_DIR + '/AccountsManager/text/PasswordForgot.txt','r')
        code=ResetPasswordCode.objects.filter(user=user).first().code
        username=user.username
        message = f.read().format(username=username, link=settings.SERVER_DOMAIN + '/AccountsManager/resetPassword/'+ username + '/' +code)
        f.close()
        
        subject = 'Reset Password DiscoTicket!'
        email_from = settings.EMAIL_HOST_USER
        email=user.email
        recipient_list = [email,]
        send_mail(subject, message, email_from, recipient_list, fail_silently=False)
        return True
    except Exception as e:
        return False
        
def forgotPasswordManager(request, username, resetPasswordCode):
    
    if request.method=='POST':
        form=PasswordForm(request.POST)
        
        if form.is_valid():
            
            user=User.objects.get(username=username)
            password=form.cleaned_data['password']
            
            resetPassCodeIstance=ResetPasswordCode.objects.filter(user=user).filter(code=resetPasswordCode)
            
            if resetPassCodeIstance.exists(): 
                
                user.set_password(password)
                user.save()
                resetPassCodeIstance.first().delete()
                return HttpResponse("your password is updated!")
            else:
                return JsonResponse({
                    'success':False,
                    "errors":'no code in the database!'
                })
    else:
        form=PasswordForm()
        return render(request, "AccountsManager/ResetPassword.html", {"form": form,
                                                                      "username": username,
                                                                      "code":  resetPasswordCode})
        
def removeResetPassCode(user):
    
        resetPassIstance=ResetPasswordCode.objects.filter(user=user)
        if resetPassIstance.exists():
            resetPassIstance.first().delete()
        else:
            logger = logging.getLogger("mylogger")
            logger.info("code already used")

def isSessionActive(key):
    try:
        session = Session.objects.get(session_key=key)
        session.get_decoded()[SESSION_KEY]
        return True
    except (Session.DoesNotExist, KeyError):
        return False

def isSessionActiveRequest(request):
    #cookie=SimpleCookie()
    
    try:
        
        #cookie.load(request.META['HTTP_COOKIE'])
        cookie=request.COOKIES
        
        #per altri valori tipo path, expires date 
        """
        c['cookie-name'].keys()
        ... 
        comment,domain,secure,expires,max-age,version,path,httponly

        expires = c['cookie-name']['expires']
        """
        
        key=cookie['sessionid']
        
        if(isSessionActive(key)):
            
            response_data={}
            response_data['success'] = True
            response_data['message'] = "Authenticated"
            return HttpResponse(json.dumps(response_data), content_type="application/json")
        else:
            response_data={}
            response_data['success'] = False
            response_data['message'] = "Not Active"
            return HttpResponse(json.dumps(response_data), content_type="application/json")
    except:
        
        response_data={}
        response_data['success'] = False
        response_data['message'] = "Not Active"
        return HttpResponse(json.dumps(response_data), content_type="application/json")
 
def getLoggedUser(request):
    logger = logging.getLogger("mylogger")
    if request.method == 'POST':
        form = GetUserByIdForm(request.POST)
        if form.is_valid():
            id=form.cleaned_data["id"]
            
            user=User.objects.get(id=id)
            response_data={}
            response_data["id"]=user.id
            response_data["name"]=user.first_name
            response_data["surname"]=user.last_name
            response_data["email"]=user.email
            
            return HttpResponse(json.dumps(response_data), content_type="application/json")
            
            
def myLogin(request):
    if request.method == 'POST':
        form = LoginForm(request.POST)
        if form.is_valid():
            username = request.POST['username']
            password = request.POST['password']
            user = authenticate(request, username=username, password=password, backend='django.contrib.auth.backends.ModelBackend')
            
            response_data = {}
            
            if user is not None:
                
                if not EmailConfirmationCode.objects.get(user=user).flag:
                    response_data['success'] = False
                    response_data['message'] = "Email not verified"
                    return HttpResponse(json.dumps(response_data), content_type="application/json")
                
                login(request, user, backend='django.contrib.auth.backends.ModelBackend')
                response_data['success'] = True
                response_data['message'] = "Signed in"
                response_data['name'] = user.first_name
                response_data['surname']=user.last_name
                response_data['id']=user.id
                #response_data['email'] = user.email
                return HttpResponse(json.dumps(response_data), content_type="application/json")
            else:
                response_data = {}
                response_data['success'] = False
                response_data['message'] = "Invalid credentials"
                return HttpResponse(json.dumps(response_data), content_type="application/json")
        else:
            return JsonResponse({
                'success':False,
                "message":dict(form.errors.items())
            })
    
    elif request.method == 'GET':
        form = LoginForm()
        
        return render(request, 'AccountsManager/Login.html', {'form': form})
    
@login_required
def logOut(request):
    
    logout(request)
    response_data = {}
    response_data['success'] = True
    response_data['message'] = "Logged out"
    response_data['result_id'] = '200'
    return HttpResponse(json.dumps(response_data), content_type="application/json")

@login_required
def deleteAccountRequest(request):
    if request.method == 'POST':
        form = DeleteAccountForm(request.POST)
        if form.is_valid():
            username = request.POST['username']
            password = request.POST['password']
            user = authenticate(request, username=username, password=password, backend='django.contrib.auth.backends.ModelBackend')
            if user is not None:
                
                user.delete()
                response_data = {}
                response_data['success'] = True
                response_data['message'] = "Account deleted"
                response_data['result_id'] = '200'
                return HttpResponse(json.dumps(response_data), content_type="application/json")
            
            return errors_response.permission_denied(request,None)
            
            
    elif request.method == 'GET':
        form = DeleteAccountForm()
        
    return render(request, 'AccountsManager/DeleteAccount.html', {'form': form})

@login_required
def changePassword(request):
    if request.method == 'POST':
        form = ChangePasswordForm(request.POST)
        if form.is_valid():

            oldPass=form.cleaned_data['oldPassword']
            newPass=form.cleaned_data['newPassword']
            #controllo inutile l'user è sempre loggato visto il tag login required
            if request.user.is_authenticated:
                user=request.user
                if user.check_password(oldPass):
                    user.set_password(newPass)
                    user.save()
                    response_data = {}
                    response_data['success'] = True
                    response_data['message'] = "Password Changed!"
                    return HttpResponse(json.dumps(response_data), content_type="application/json")
                else:
                    response_data = {}
                    response_data['success'] = False
                    response_data['message'] = "wrong old password!"
                    return HttpResponse(json.dumps(response_data), content_type="application/json")


        
        else:
            return JsonResponse({
                'success':False,
                "errors":dict(form.errors.items())
            })
    else:
        return render(request, "AccountsManager/ChangePassword.html")