def global_settings(request):
    return {'BLOCK_SIZE': 1}[request]
