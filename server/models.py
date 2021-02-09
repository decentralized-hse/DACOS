from django.db import models

# Create your models here.
class PublicUser(models.Model):
    username = models.CharField(max_length = 50)
    cell_id = models.IntegerField()
    public_rsa_n = models.IntegerField()
    public_rsa_e = models.IntegerField()
    g_in_big_power = models.IntegerField()

    