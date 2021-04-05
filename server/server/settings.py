def global_settings(request):
    return {'BLOCK_SIZE': 32}[request]
