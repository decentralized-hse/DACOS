from .models import Server, ServerKeys
import requests
import rsa
import os


try:
    # Generate RSA if there is none.
    keys = list(ServerKeys.objects.all())
    key = None
    if len(keys) == 0:
        (pub_key, private_key) = rsa.newkeys(2048)
        pub_key = pub_key._save_pkcs1_pem().decode("utf-8")
        private_key = private_key._save_pkcs1_pem().decode("utf-8")
        key = ServerKeys(public_key=pub_key, private_key=private_key)
        key.save()
    else:
        key = keys[0]
    SERVER_PRIVATE_KEY = rsa.PrivateKey._load_pkcs1_pem(key.private_key)
    # Update servers list.
    if os.getenv('THIS_SERVER') == os.getenv('ADMIN_SERVER'):
        try:
            Server.objects.get(url=os.getenv('THIS_SERVER'))
        except Server.DoesNotExist:
            serv = Server(url=os.getenv('THIS_SERVER'), public_key=key.public_key)
            serv.save()
    else:
        requests.post(os.getenv('ADMIN_SERVER') + '/servers/add', {'url': os.getenv('THIS_SERVER'),
                                                                   'public_key': key.public_key})
except:
    # In case we are running migrations we would fall here.
    # So we are not doing anything.
    pass