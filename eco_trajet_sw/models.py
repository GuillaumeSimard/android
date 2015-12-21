'''
Created on 2015-03-03

@author: Remy Huot
@author: Guillaume Simard
'''

from google.appengine.ext import ndb


class Parcours(ndb.Model):
    nomParcours = ndb.StringProperty(required=True, indexed=False)
    nbPlaceDisponible = ndb.IntegerProperty(required=True, indexed=False)
    nbPlacePrise = ndb.IntegerProperty(required=True, indexed=False)
    dateParcour = ndb.StringProperty(required=True, indexed=False)
    heure = ndb.StringProperty(required=True, indexed=False)
    coutPersonne = ndb.FloatProperty(required=True, indexed=False)
    idRegion = ndb.IntegerProperty(required=True, indexed=False)
    coordonneDepart = ndb.StringProperty(required=True, indexed=False)
    coordonneArrive = ndb.StringProperty(required=True, indexed=False)
    nomConducteur = ndb.StringProperty(required=True, indexed=True)
    listePassager = ndb.StringProperty(repeated=True, required=False)


class Utilisateurs(ndb.Model):
    prenom = ndb.StringProperty(required=True)
    nom = ndb.StringProperty(required=True)
    noTelephone = ndb.StringProperty(required=True)
    email = ndb.StringProperty(required=True)
    motDePasse = ndb.StringProperty(required=True)
    idTypePassager = ndb.IntegerProperty(required=True)
    listeDemandeParcours = ndb.StringProperty(repeated=True, required=False)



