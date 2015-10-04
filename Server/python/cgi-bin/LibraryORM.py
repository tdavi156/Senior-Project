__author__ = 'nicholaspadgett'

from DataSource import *
from LibraryClass import *
from ItemORM import *
from ItemClass import *


class LibraryORM(object):
    def get_items_from_library(self,form):
        library_id = form["library_id"]
        user_id = form["user_id"]

        #check if library is visible or if it is owned by user
        is_library_viewable=self.check_if_library_visible(library_id,user_id)
        if not is_library_viewable:
            raise Exception("Library is private")

        item_list = self.get_items_in_library(library_id)

        return item_list

    def get_items_in_library(self,library_id):
        query = "select item_id from library_items where library_id={0}".format(library_id)
        response = select_query(query)
        options = []

        item_orm = ItemORM()
        for i in range(0, len(response)):
            options.append({"id":response[i][0]})

        results = item_orm.search_items(options, 1)
        return results

    def check_if_library_visible(self, library_id, user_id):
        query = "select user_id, visible from libraries where id={0}".format(library_id)
        response = select_query(query)
        if response[0][1] == 1:
            return True
        elif response[0][0] == user_id:
            return True
        else:
            return False

    def get_user_libraries(self, form):
        user_id = form["user_id"]
        current_user_id = form["current_user_id"]

        query = "select id,library_name, user_id, visible from libraries where user_id={0}".format(user_id)
        results = select_query(query)
        return self.get_libraries_from_response(results)

    def get_libraries_from_response(self, rows):
        results = []
        for i in range(0, len(rows)):
            if rows[i][3] == True:
                results.append(Library(rows[i]))

        return results

    def remove_item_from_library(self, form):
        item_id = form["item_id"]
        library_id = form["library_id"]
        user_id = form["user_id"]

        #make sure user owns library
        query = "select id, user_id from libraries where id={0} and user_id={1}".format(library_id, user_id)
        results = select_query(query)
        if len(results) == 0:
            raise Exception("You do not have access to modify this library")

        #remove item form library
        args = {"library_id":library_id,"item_id":item_id}
        query = "delete from library_items where library_id=%(library_id)s and item_id=%(item_id)s"
        insert_query(query, args)

        return True