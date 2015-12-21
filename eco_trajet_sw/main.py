# -*- coding: utf-8 -*-
import webapp2
import logging
import traceback
import datetime
import json
import math



from models import Parcours, Utilisateurs

from google.appengine.ext import ndb
from google.appengine.ext import db



def serialiser_pour_json(objet):
    if isinstance(objet, datetime.datetime):
        return objet.replace(microsecond=0).isoformat()
    elif isinstance(objet, datetime.date):
        return objet.isoformat()
    else:
        return objet

def distance(origin, destination):
    lat1, lon1 = origin
    lat2, lon2 = destination
    radius = 6371 # km

    dlat = math.radians(lat2-lat1)
    dlon = math.radians(lon2-lon1)
    a = math.sin(dlat/2) * math.sin(dlat/2) + math.cos(math.radians(lat1)) \
        * math.cos(math.radians(lat2)) * math.sin(dlon/2) * math.sin(dlon/2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    d = radius * c

    return d


class MainPageHandler(webapp2.RequestHandler):

    def get(self):
        self.response.headers['Content-Type'] = 'text/plain; charset=utf-8'
        self.response.out.write('Travail pratique "Service Web REST avec ' +
                                'Google App Engine" en fonction.')


class ConnexionHandler(webapp2.RequestHandler):

    def post(self):
        try:

            dude_dict_in = json.loads(self.request.body)
            cle = ndb.Key('Utilisateurs', dude_dict_in['_nomUtilisateur'])

            # Récuperation de l'utilisateur s'il existe
            dude = cle.get()

            # Si l'utilisateur n'existe pas retourner une erruer
            if dude is None or dude.motDePasse != dude_dict_in['motDePasse']:
                status = 400
                dude_dict_out = {}
                dude_dict_out['erreur'] = "Combinaison nomUtilisateur, mot de passe incorrect."
            else:
                status = 201
                dude_dict_out = dude.to_dict()
                dude_dict_out['_nomUtilisateur'] = dude_dict_in['_nomUtilisateur']
                self.response.headers['Location'] = (self.request.url + '/' +
                                                     str(dude_dict_in['_nomUtilisateur']))

            # Configuration du code de statut HTTP (201 Created).
            self.response.set_status(status)

            # Le corps de la réponse contiendra une représentation en JSON
            # de l'animal qui vient d'être créé.
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

            dude_json = json.dumps(dude_dict_out, default=serialiser_pour_json)
            self.response.out.write(dude_json)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)


class UtilisateurHandler(webapp2.RequestHandler):
    def put(self, _nomUtilisateur):
        try:
            cle = ndb.Key('Utilisateurs', _nomUtilisateur)
            utilAjout = cle.get()

            if utilAjout is None:
                status = 201
                utilAjout = Utilisateurs(key=cle)
            else:
                status = 200

            utilisateurs_dict_in = json.loads(self.request.body)
            utilAjout.prenom = utilisateurs_dict_in['prenom']
            utilAjout.nom = utilisateurs_dict_in['nom']
            utilAjout.noTelephone = utilisateurs_dict_in['noTelephone']
            utilAjout.email = utilisateurs_dict_in['email']
            utilAjout.motDePasse = utilisateurs_dict_in['motDePasse']
            utilAjout.idTypePassager = utilisateurs_dict_in['idTypePassager']
            self.response.set_status(status)

            cle_Utilisateurs = utilAjout.put()

            self.response.set_status(status)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            Utilisateur_dict = utilAjout.to_dict()
            Utilisateur_dict['_nomUtilisateur'] = cle_Utilisateurs.id()
            json_data = json.dumps(Utilisateur_dict, default=serialiser_pour_json)
            self.response.out.write(json_data)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

class ParcoursHandler(webapp2.RequestHandler):

    def get(self):
            try:
                liste_parcours = []
                list_ajout = []
                requete = Parcours.query()
                
                dateParcour = self.request.get('dateParcour')
                coordoDemanderDepart = self.request.get('coordoDepart')
                coordoDemanderArriver = self.request.get('coordoArrive') 
                for parcours in requete:
                    
                    if str(dateParcour) is not "":
                        if parcours.dateParcour == str(dateParcour): 
                            
                            if parcours.nbPlacePrise < parcours.nbPlaceDisponible:
                                
                                if coordoDemanderDepart is not "" and coordoDemanderArriver is not "":
                                    coordoDepart = parcours.coordonneDepart.split(';')
                                    lat1 = coordoDepart[0]
                                    long1 = coordoDepart[1]
                
                                    coordoArrive = parcours.coordonneArrive.split(';')
                                    lat2 = coordoArrive[0]
                                    long2 = coordoArrive[1]
                                    
                                    coordoDepartDemande = coordoDemanderDepart.split(' ')
                                    latDemande1 = coordoDepartDemande[0]
                                    longDemande1 = coordoDepartDemande[1]
                                                    
                                    coordoArriveDemande = coordoDemanderArriver.split(' ')
                                    latDemande2 = coordoArriveDemande[0]
                                    longDemande2 = coordoArriveDemande[1]
                                    logging.info(float(longDemande2))
                                    if (distance((float(lat1), float(long1)),(float(latDemande1), float(longDemande1))) <= 3.5) and (distance((float(lat2), float(long2)),(float(latDemande2), float(longDemande2))) <= 3.5):
        
                                        pers_dict = parcours.to_dict()
                                        pers_dict['idParcours'] = parcours.key.id()
                                        list_ajout.append(pers_dict)

                                else:
                                    pers_dict = parcours.to_dict()
                                    pers_dict['idParcours'] = parcours.key.id()
                                    list_ajout.append(pers_dict)
                                    
                    else:
                            if coordoDemanderDepart is not "" and coordoDemanderArriver is not "":
                                
                                coordoDepart = parcours.coordonneDepart.split(';')
                                lat1 = coordoDepart[0]
                                long1 = coordoDepart[1]
                
                                coordoArrive = parcours.coordonneArrive.split(';')
                                lat2 = coordoArrive[0]
                                long2 = coordoArrive[1]
                                
                                coordoDepartDemande = coordoDemanderDepart.split(' ')
                                latDemande1 = coordoDepartDemande[0]
                                longDemande1 = coordoDepartDemande[1]
                                                
                                coordoArriveDemande = coordoDemanderDepart.split(' ')
                                latDemande2 = coordoArriveDemande[0]
                                longDemande2 = coordoArriveDemande[1]
                                logging.info(coordoDemanderDepart)
                                
                                if distance((float(lat1), float(long1)),(float(latDemande1), float(longDemande1))) <= 5 and distance((float(lat2), float(long2)),(float(latDemande2), float(longDemande2))) <= 5:
                                    
                                    pers_dict = parcours.to_dict()
                                    pers_dict['idParcours'] = parcours.key.id()
                                    list_ajout.append(pers_dict)
                            else:
                                
                                pers_dict = parcours.to_dict()
                                pers_dict['idParcours'] = parcours.key.id()
                                list_ajout.append(pers_dict)
                                        

                json_data = json.dumps(list_ajout, default=serialiser_pour_json)
    
                self.response.set_status(200)
                self.response.headers['Content-Type'] = ('application/json;' +
                                                         ' charset=utf-8')
                self.response.out.write(json_data)

            except (db.BadValueError, ValueError, KeyError):
                logging.error('%s', traceback.format_exc())
                self.error(400)

            except Exception:
                logging.error('%s', traceback.format_exc())
                self.error(500)


class ParcourHandler(webapp2.RequestHandler):

    def get(self, _nomUtilisateur,_id=None ):
        try: 
            if _id is not None:
                
                cle_get_Parcours = ndb.Key('Parcours',long(_id))
                get_Parcours = cle_get_Parcours.get()
               
                if get_Parcours is None:
                    self.error(404)
                    return
                elif _nomUtilisateur== get_Parcours.nomConducteur:
                    status = 200
                else:
                    self.error(401)
                    return
                dict_parcours_out = get_Parcours.to_dict()
                dict_parcours_out['_id'] = cle_get_Parcours.id()
                json_data = json.dumps(dict_parcours_out, default=serialiser_pour_json)
                self.response.set_status(status)

            else:
                cle_util = ndb.Key("Utilisateurs",_nomUtilisateur)
                util = cle_util.get()
                
                if util is None:
                    self.error(404)
                    return 
                
                liste_Parcours = []
                requete = Parcours.query()

                if util.idTypePassager == 2:
                    for parcours in requete:
                        if util.prenom in parcours.listePassager:
                              pers_dict = parcours.to_dict()
                              pers_dict['_id'] = parcours.key.id()
                              liste_Parcours.append(pers_dict)
                else:
                    requete = requete.filter(Parcours.nomConducteur == _nomUtilisateur)

                    for unParcour in requete:
                        pers_dict = unParcour.to_dict()
                        pers_dict['_id'] = unParcour.key.id()
                        # Ajout de la personne dans la liste.
                        liste_Parcours.append(pers_dict)

                json_data = json.dumps(liste_Parcours, default=serialiser_pour_json)

                self.response.set_status(200)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            self.response.out.write(json_data)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def put(self,_id, _nomUtilisateur):
        try:
            cle_Parcours = ndb.Key('Parcours', long(_id))
            parcourAjout = cle_Parcours.get()

            if parcourAjout is None:
                self.error(404)
                return
            else:
                status = 200
            parcour_dict_in = json.loads(self.request.body)
            parcourAjout.nomParcours = parcour_dict_in['nomParcour']
            parcourAjout.nbPlaceDisponible = parcour_dict_in['nbPlaceDisponible']
            parcourAjout.nbPlacePrise = parcour_dict_in['nbPlacePrise']
            parcourAjout.dateParcour = parcour_dict_in['dateParcour']
            parcourAjout.heure = parcour_dict_in['heure']
            parcourAjout.coutPersonne = parcour_dict_in['coutPersonne']
            parcourAjout.idRegion = parcour_dict_in['idRegion']
            parcourAjout.coordonneDepart = parcour_dict_in['coordonneDepart']
            parcourAjout.coordonneArrive = parcour_dict_in['coordonneArrive']
            parcourAjout.nomConducteur = _nomUtilisateur

            cle_Parcours = parcourAjout.put()

            self.response.set_status(status)

            parcours_dict = parcourAjout.to_dict()

            parcours_dict['idParcours'] = cle_Parcours.id()

            parcours_json = json.dumps(parcours_dict, default=serialiser_pour_json)

            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

            self.response.out.write(parcours_json)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def post(self, _nomUtilisateur):
        try:
            
            parcoursAjout = Parcours()
            parcours_dict_in = json.loads(self.request.body)
            parcoursAjout.nomParcours= parcours_dict_in['nomParcour']
            parcoursAjout.nbPlaceDisponible = parcours_dict_in['nbPlaceDisponible']
            parcoursAjout.nbPlacePrise = parcours_dict_in['nbPlacePrise']
            parcoursAjout.dateParcour = parcours_dict_in['dateParcour']
            parcoursAjout.heure = parcours_dict_in['heure']
            parcoursAjout.coutPersonne = parcours_dict_in['coutPersonne']
            parcoursAjout.idRegion = parcours_dict_in['idRegion']
            parcoursAjout.coordonneDepart = parcours_dict_in['coordonneDepart']
            parcoursAjout.coordonneArrive = parcours_dict_in['coordonneArrive']
            parcoursAjout.nomConducteur = _nomUtilisateur

            cle_parcours = parcoursAjout.put()
            self.response.set_status(201)

            self.response.headers['Location'] = (self.request.url + '/' +
                                                 str(cle_parcours.id()))
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            dict_parcours_out = parcoursAjout.to_dict()
            dict_parcours_out['_id'] = cle_parcours.id()
            json_data_post = json.dumps(dict_parcours_out, default=serialiser_pour_json)

            self.response.out.write(json_data_post)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

 
    def delete(self, _id):
        try:
            # Vérifié que le site existe et le propriétaire existe.
            cle_Parcours = ndb.Key('Parcours', long(_id))
            leParcours = cle_Parcours.get()

            if leParcours is None:
                self.error(404)
                return
            leParcours.key.delete()


            self.response.set_status(204)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)


class ParcoursPassageHandler(webapp2.RequestHandler):
    def get(self, _id):
        try:
            cle_dude = ndb.Key('Parcours', long(_id))
            passager = cle_dude.get()
            if passager is None:
                self.error(404)
                return
            else:
                status = 200
                json_data = json.dumps(passager.listePassager,
                                       default=serialiser_pour_json)

            self.response.set_status(status)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def put(self, _id, conducteur,passager ):
        try:
            cle_Parcours = ndb.Key('Parcours', long(_id))
            cle_conducteur = ndb.Key('Utilisateurs', conducteur)
            cle_passager = ndb.Key('Utilisateurs', passager)

            parcours = cle_Parcours.get()
            conducteur  = cle_conducteur.get()
            passager = cle_passager.get()

            if parcours is None:
                self.error(404)
                return
            elif ("0" + passager.prenom not in parcours.listePassager):
                parcours.listePassager.append(passager.prenom) 
                parcours.nbPlacePrise += 1
                status = 201;
            else:
                status = 200;
                if "1" + passager.prenom in parcours.listePassager:
                    parcours.listePassager.remove("1" + passager.prenom)
            parcours.put()

            self.response.set_status(status)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def delete(self, _id, conducteur,passager):
        try:
            cle_Parcour = ndb.Key('Parcours', long(_id))
            cle_conducteur = ndb.Key('Utilisateur', conducteur)
            cle_passager = ndb.Key('Utilisateurs', passager)
            parcours = cle_Parcour.get()
            conducteur = cle_conducteur.get()
            passager = cle_passager.get()

            if parcours is None:
                self.error(404)
                return
            else:
                if passager.prenom in parcours.listePassager:
                    parcours.listePassager.remove(passager.prenom)
                    parcours.nbPlacePrise -= 1 
                    parcours.put()

            self.response.set_status(204)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)



class DemandeParcoursHandler(webapp2.RequestHandler):
    def get(self, conducteur):
        try:
            cle_dude = ndb.Key('Utilisateurs', conducteur)
            util = cle_dude.get()
            if util is None:
                self.error(404)
                return
            else:
                status = 200
                json_data = json.dumps(util.listeDemandeParcours,
                                       default=serialiser_pour_json)

            self.response.set_status(status)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')
            self.response.out.write(json_data)
        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def put(self,_id, conducteur, demandeur):
        try:
            cle_conducteur = ndb.Key('Utilisateurs', conducteur)

            conducteur = cle_conducteur.get()

            if conducteur is None:
                self.error(404)
                return
            else:
                status = 201;

                # le chiffre 0 c'est qu'il n'as pas été notifié.
                conducteur.listeDemandeParcours.append(str(_id) + "; " + demandeur)
                conducteur.put()

            self.response.set_status(status)
            self.response.headers['Content-Type'] = ('application/json;' +
                                                     ' charset=utf-8')

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

    def delete(self,_id, conducteur, demandeur):
        try:
            cle_conducteur = ndb.Key('Utilisateurs', conducteur)

            conducteur = cle_conducteur.get()
            if conducteur is None or demandeur is None:
                self.error(404)
                return
            else:
                if str(_id) + "; " + demandeur  in conducteur.listeDemandeParcours:
                    conducteur.listeDemandeParcours.remove(str(_id) + "; " + demandeur)
                    conducteur.put()
                else:
                    self.error(400)
                    return 
            self.response.set_status(204)

        except (db.BadValueError, ValueError, KeyError):
            logging.error('%s', traceback.format_exc())
            self.error(400)

        except Exception:
            logging.error('%s', traceback.format_exc())
            self.error(500)

app = webapp2.WSGIApplication(
    [
        webapp2.Route(r'/',
                      handler=MainPageHandler,
                      methods=['GET']),
        webapp2.Route(r'/utilisateurs/<_nomUtilisateur>',
                      handler=UtilisateurHandler,
                      methods=['PUT']),
        webapp2.Route(r'/connexion',
                      handler=ConnexionHandler,
                      methods=['POST']),
        webapp2.Route(r'/parcours',
                      handler=ParcoursHandler,
                      methods=['GET']),
        webapp2.Route(r'/parcours/<_id>',
                      handler=ParcourHandler,
                      methods=['DELETE']),
        webapp2.Route(r'/utilisateurs/<_nomUtilisateur>/Parcours',
                      handler=ParcourHandler,
                      methods=['POST', 'GET']),
        webapp2.Route(r'/utilisateurs/<_nomUtilisateur>/Parcours/<_id>',
                      handler=ParcourHandler,
                      methods=['GET', 'PUT']),
        webapp2.Route(r'/parcours/<_id>/passager',
                      handler=ParcoursPassageHandler,
                      methods=['GET']),
        webapp2.Route(r'/utilisateurs/<conducteur>/parcours/<_id>/passager/<passager>',
                      handler=ParcoursPassageHandler,
                      methods=['PUT', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<conducteur>/parcours/<_id>/demandeParcours/<demandeur>',
                      handler=DemandeParcoursHandler,
                      methods=['PUT', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<conducteur>/demandeParcour',
                      handler=DemandeParcoursHandler,
                      methods=['GET']),
    ],
    debug=True)
