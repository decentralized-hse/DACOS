from ..models import PublicUser, Server, ServerKeys, EncodedMessage


def valid_username(username):
    users = PublicUser.objects.values('username')
    for user in users:
        if username == user['username']:
            return False
    return True


def server_exists(url):
    servers = Server.objects.values('url')
    for server in servers:
        if url == server['url']:
            return True
    return False
