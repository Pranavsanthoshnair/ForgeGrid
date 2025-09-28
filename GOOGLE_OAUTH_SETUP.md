# Google OAuth Setup Guide for ForgeGrid

## 🚀 Real Google OAuth Integration Complete!

Your ForgeGrid application now has **real Google OAuth integration** instead of mock tokens. Here's how to set it up:

## 📋 Prerequisites

1. **Google Cloud Console Account** - You need a Google account
2. **Supabase Project** - Already configured ✅
3. **Google OAuth Credentials** - Need to be created

## 🔧 Step 1: Create Google OAuth Credentials

### 1.1 Go to Google Cloud Console
1. Visit [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the **Google+ API** (or **Google Identity API**)

### 1.2 Configure OAuth Consent Screen
1. Go to **APIs & Services > OAuth consent screen**
2. Choose **External** user type
3. Fill in required fields:
   - **App name**: ForgeGrid
   - **User support email**: Your email
   - **Developer contact**: Your email
4. Add scopes:
   - `openid`
   - `email`
   - `profile`

### 1.3 Create OAuth Credentials
1. Go to **APIs & Services > Credentials**
2. Click **Create Credentials > OAuth 2.0 Client IDs**
3. Choose **Desktop application**
4. Set **Name**: ForgeGrid Desktop App
5. **Authorized redirect URIs**: `http://localhost:8080/callback`
6. Click **Create**

### 1.4 Copy Credentials
1. Copy the **Client ID** and **Client Secret**
2. You'll need these for configuration

## ⚙️ Step 2: Configure ForgeGrid

### 2.1 Update config.properties
Edit `src/main/resources/config.properties`:

```properties
# Google OAuth Settings
google.oauth.client.id=YOUR_ACTUAL_CLIENT_ID.apps.googleusercontent.com
google.oauth.client.secret=YOUR_ACTUAL_CLIENT_SECRET
google.oauth.redirect.uri=http://localhost:8080/callback
google.oauth.scope=openid email profile
```

### 2.2 Replace Placeholder Values
- Replace `YOUR_ACTUAL_CLIENT_ID` with your Google Client ID
- Replace `YOUR_ACTUAL_CLIENT_SECRET` with your Google Client Secret

## 🔗 Step 3: Configure Supabase

### 3.1 Enable Google Provider
1. Go to your Supabase dashboard
2. Navigate to **Authentication > Providers**
3. Enable **Google** provider
4. Add your Google OAuth credentials:
   - **Client ID**: Same as above
   - **Client Secret**: Same as above

### 3.2 Configure Redirect URLs
In Supabase, add these redirect URLs:
- `http://localhost:8080/callback`
- `http://localhost:3000/callback` (if needed)

## 🧪 Step 4: Test the Integration

### 4.1 Run the Application
```bash
mvn exec:java
```

### 4.2 Test Google Sign-In
1. Click the **"Sign in with Google"** button
2. Browser should open with Google OAuth
3. Sign in with your Google account
4. Copy the authorization code from browser
5. Paste it in the dialog
6. Should authenticate successfully with Supabase!

## 🎯 How It Works Now

### Real OAuth Flow:
1. **User clicks Google Sign-In** → Opens browser
2. **Google OAuth page** → User signs in
3. **Authorization code** → User copies from browser
4. **Exchange for token** → App gets Google access token
5. **Supabase authentication** → Uses Google token
6. **User profile created** → Stored in Supabase profiles table
7. **Online authentication** → Data goes to Supabase, not local file!

## 🔍 Troubleshooting

### Common Issues:

#### 1. "Invalid client" error
- **Solution**: Check Client ID and Secret in config.properties

#### 2. "Redirect URI mismatch" error
- **Solution**: Ensure `http://localhost:8080/callback` is added to Google Console

#### 3. "Scope not authorized" error
- **Solution**: Add `openid email profile` scopes in Google Console

#### 4. Supabase authentication fails
- **Solution**: Verify Google provider is enabled in Supabase

## 🎉 Benefits of Real OAuth

✅ **Real Google authentication** - No more mock tokens!
✅ **Secure token exchange** - Proper OAuth 2.0 flow
✅ **User data in Supabase** - Profiles stored online
✅ **Professional integration** - Industry-standard OAuth
✅ **Browser-based flow** - Familiar Google sign-in experience

## 📝 Next Steps

1. **Set up your Google OAuth credentials** (follow steps above)
2. **Update config.properties** with real credentials
3. **Test the Google Sign-In button**
4. **Verify user data goes to Supabase** (not offline_profiles.json)

Your ForgeGrid app now has **real Google OAuth integration**! 🚀
