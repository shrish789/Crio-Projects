#!/bin/bash

mongoimport --db memes --collection memes --drop --jsonArray --file ./sample-meme-data.json