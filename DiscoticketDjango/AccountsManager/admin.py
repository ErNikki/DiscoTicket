from django.contrib import admin
from .models.emailConfirmationCode import EmailConfirmationCode
from .models.resetPasswordCode import ResetPasswordCode

# Register your models here.
admin.site.register(EmailConfirmationCode)
admin.site.register(ResetPasswordCode)
