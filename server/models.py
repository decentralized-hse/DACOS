from django.db import models

# Create your models here.
class PublicUser(models.Model):
    username = models.CharField(max_length = 50)
    cell_id = models.IntegerField()
    public_rsa_n = models.CharField(max_length = 300)
    public_rsa_e = models.CharField(max_length = 300)
    g_in_big_power = models.CharField(max_length = 300)

    