import { useState } from "react";
import { useLocation } from "wouter";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { ArrowLeft, Save, LogOut, Lock, User, Mail, Phone, Building, Shield } from "lucide-react";

export default function Profile() {
  const [, setLocation] = useLocation();
  
  const currentUser = JSON.parse(localStorage.getItem("currentUser") || "{}");
  const [profileData, setProfileData] = useState({
    id: currentUser.id || 1,
    nom: currentUser.nom || "Jean Mukendi",
    email: currentUser.email || "jean.mukendi@example.com",
    telephone: currentUser.telephone || "+243 900 000 000",
    organisation: currentUser.organisation || "Association Musep50",
    role: currentUser.role || "Administrateur",
  });

  const [pinData, setPinData] = useState({
    currentPin: "",
    newPin: "",
    confirmPin: "",
  });

  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [isChangingPin, setIsChangingPin] = useState(false);

  const handleProfileUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch(`/api/users/${profileData.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nom: profileData.nom,
          email: profileData.email,
          telephone: profileData.telephone,
          organisation: profileData.organisation,
        }),
      });
      if (response.ok) {
        setIsEditingProfile(false);
      }
    } catch (error) {
      console.error("Error updating profile:", error);
    }
  };

  const handlePinChange = async (e: React.FormEvent) => {
    e.preventDefault();
    if (pinData.newPin !== pinData.confirmPin) {
      alert("Les PINs ne correspondent pas");
      return;
    }
    if (pinData.newPin.length !== 4) {
      alert("Le PIN doit contenir 4 chiffres");
      return;
    }
    
    // TODO: Add API endpoint to change PIN
    console.log("Changing PIN");
    setPinData({ currentPin: "", newPin: "", confirmPin: "" });
    setIsChangingPin(false);
  };

  const handleLogout = () => {
    localStorage.removeItem("currentUser");
    window.location.href = "/login";
  };

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <div className="min-h-screen bg-background pb-6">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-card border-b border-border">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center gap-3">
            <Button size="icon" variant="ghost" onClick={() => setLocation("/")} data-testid="button-back">
              <ArrowLeft className="w-5 h-5" />
            </Button>
            <div className="flex-1">
              <h1 className="text-xl font-bold">Mon profil</h1>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6 max-w-3xl space-y-6">
        {/* Profile Header */}
        <Card>
          <CardContent className="pt-6">
            <div className="flex flex-col sm:flex-row items-center gap-6">
              <Avatar className="w-24 h-24">
                <AvatarFallback className="text-2xl bg-primary text-primary-foreground">
                  {getInitials(profileData.nom)}
                </AvatarFallback>
              </Avatar>
              <div className="flex-1 text-center sm:text-left space-y-2">
                <h2 className="text-2xl font-bold" data-testid="text-user-name">{profileData.nom}</h2>
                <p className="text-muted-foreground">{profileData.role}</p>
                <p className="text-sm text-muted-foreground">{profileData.organisation}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Personal Information */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <User className="w-5 h-5" />
                  Informations personnelles
                </CardTitle>
                <CardDescription>Gérez vos informations de profil</CardDescription>
              </div>
              {!isEditingProfile && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setIsEditingProfile(true)}
                  data-testid="button-edit-profile"
                >
                  Modifier
                </Button>
              )}
            </div>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleProfileUpdate} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="nom" className="flex items-center gap-2">
                  <User className="w-4 h-4" />
                  Nom complet
                </Label>
                <Input
                  id="nom"
                  value={profileData.nom}
                  onChange={(e) => setProfileData({ ...profileData, nom: e.target.value })}
                  disabled={!isEditingProfile}
                  data-testid="input-name"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="email" className="flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  Email
                </Label>
                <Input
                  id="email"
                  type="email"
                  value={profileData.email}
                  onChange={(e) => setProfileData({ ...profileData, email: e.target.value })}
                  disabled={!isEditingProfile}
                  data-testid="input-email"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="telephone" className="flex items-center gap-2">
                  <Phone className="w-4 h-4" />
                  Téléphone
                </Label>
                <Input
                  id="telephone"
                  type="tel"
                  value={profileData.telephone}
                  onChange={(e) => setProfileData({ ...profileData, telephone: e.target.value })}
                  disabled={!isEditingProfile}
                  data-testid="input-phone"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="organisation" className="flex items-center gap-2">
                  <Building className="w-4 h-4" />
                  Organisation
                </Label>
                <Input
                  id="organisation"
                  value={profileData.organisation}
                  onChange={(e) => setProfileData({ ...profileData, organisation: e.target.value })}
                  disabled={!isEditingProfile}
                  data-testid="input-organisation"
                />
              </div>

              {isEditingProfile && (
                <div className="flex gap-3 pt-4">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setIsEditingProfile(false)}
                    className="flex-1"
                    data-testid="button-cancel-edit"
                  >
                    Annuler
                  </Button>
                  <Button type="submit" className="flex-1" data-testid="button-save-profile">
                    <Save className="w-4 h-4 mr-2" />
                    Enregistrer
                  </Button>
                </div>
              )}
            </form>
          </CardContent>
        </Card>

        {/* Security Settings */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Shield className="w-5 h-5" />
              Sécurité
            </CardTitle>
            <CardDescription>Gérez vos paramètres de sécurité</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {!isChangingPin ? (
              <div className="flex items-center justify-between">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <Lock className="w-4 h-4 text-muted-foreground" />
                    <p className="font-medium">Code PIN</p>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    Modifiez votre code PIN de sécurité
                  </p>
                </div>
                <Button
                  variant="outline"
                  onClick={() => setIsChangingPin(true)}
                  data-testid="button-change-pin"
                >
                  Modifier
                </Button>
              </div>
            ) : (
              <form onSubmit={handlePinChange} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="currentPin">PIN actuel</Label>
                  <Input
                    id="currentPin"
                    type="password"
                    inputMode="numeric"
                    maxLength={4}
                    value={pinData.currentPin}
                    onChange={(e) => setPinData({ ...pinData, currentPin: e.target.value })}
                    placeholder="••••"
                    required
                    data-testid="input-current-pin"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="newPin">Nouveau PIN</Label>
                  <Input
                    id="newPin"
                    type="password"
                    inputMode="numeric"
                    maxLength={4}
                    value={pinData.newPin}
                    onChange={(e) => setPinData({ ...pinData, newPin: e.target.value })}
                    placeholder="••••"
                    required
                    data-testid="input-new-pin"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirmPin">Confirmer le nouveau PIN</Label>
                  <Input
                    id="confirmPin"
                    type="password"
                    inputMode="numeric"
                    maxLength={4}
                    value={pinData.confirmPin}
                    onChange={(e) => setPinData({ ...pinData, confirmPin: e.target.value })}
                    placeholder="••••"
                    required
                    data-testid="input-confirm-pin"
                  />
                </div>

                <div className="flex gap-3">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => {
                      setIsChangingPin(false);
                      setPinData({ currentPin: "", newPin: "", confirmPin: "" });
                    }}
                    className="flex-1"
                    data-testid="button-cancel-pin"
                  >
                    Annuler
                  </Button>
                  <Button type="submit" className="flex-1" data-testid="button-save-pin">
                    <Save className="w-4 h-4 mr-2" />
                    Enregistrer
                  </Button>
                </div>
              </form>
            )}

            <Separator />

            {/* Logout */}
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="outline" className="w-full" data-testid="button-logout">
                  <LogOut className="w-4 h-4 mr-2" />
                  Se déconnecter
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Se déconnecter</AlertDialogTitle>
                  <AlertDialogDescription>
                    Êtes-vous sûr de vouloir vous déconnecter ? Vous devrez entrer votre PIN pour vous reconnecter.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel data-testid="button-cancel-logout">Annuler</AlertDialogCancel>
                  <AlertDialogAction onClick={handleLogout} data-testid="button-confirm-logout">
                    Se déconnecter
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </CardContent>
        </Card>

        {/* Statistics Card */}
        <Card>
          <CardHeader>
            <CardTitle>Statistiques personnelles</CardTitle>
            <CardDescription>Votre activité sur la plateforme</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <p className="text-sm text-muted-foreground">Opérations créées</p>
                <p className="text-2xl font-bold" data-testid="stat-operations">12</p>
              </div>
              <div className="space-y-2">
                <p className="text-sm text-muted-foreground">Paiements enregistrés</p>
                <p className="text-2xl font-bold" data-testid="stat-payments">156</p>
              </div>
              <div className="space-y-2">
                <p className="text-sm text-muted-foreground">Montant collecté</p>
                <p className="text-2xl font-bold text-chart-2" data-testid="stat-collected">450 000 FCFA</p>
              </div>
              <div className="space-y-2">
                <p className="text-sm text-muted-foreground">Membre depuis</p>
                <p className="text-2xl font-bold" data-testid="stat-member-since">Janv. 2024</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}
