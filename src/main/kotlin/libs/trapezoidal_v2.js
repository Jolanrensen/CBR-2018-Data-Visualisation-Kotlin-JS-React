var text = new PointText(new Point(10, 10));
text.fontSize = '10px';
text.fillColor = 'black';

var obstacles = [];
var drawables = [];
var polygonPoints = [];
var trapezoidlist = [];
var vertexList = [];

var canvas = document.getElementById('myCanvas');

//////////////////////////   Mouse Interactions ///////////////////////
var segment, path;
var hitOptions = {segments: true, fill: true, stroke: true, tolerance: 15};

function onMouseDown(event) {

    segment = path = null;

    var hitResult = project.hitTest(event.point, hitOptions);
    if (hitResult) {
        path = hitResult.item;
        if (hitResult.type == 'segment')
            segment = hitResult.segment
    }
}

function onMouseMove(event) {
    if (Key.isDown('control'))
        text.content = 'Pos:' + event.point.toString();
    else
        text.content = '';

    project.activeLayer.selected = false;
    if (event.item) {
        if (event.item.name == 'obstacle') {
            event.item.bringToFront();
            event.item.selected = true;
        } else if (Key.isDown('shift')) {
            clearDrawables();
            window.draw();

            var closestTrapezoid = getClosest(event.point, trapezoidlist);

            var newVertex = vertexList.mypush(event.point);

            var vertexes = [];

            if (closestTrapezoid.leftNeighbours.length == 0) // first trapezoid
            {
                var vertex1 = vertexList.mypush(closestTrapezoid.center);
                vertexes.push(vertex1)
            } else if (closestTrapezoid.leftNeighbours.length == 1) {
                var vertex1 = vertexList.mypush(closestTrapezoid.edge1.middle);
                vertexes.push(vertex1)
            } else if (closestTrapezoid.leftNeighbours.length == 2) {
                var vertex1 = vertexList.mypush(closestTrapezoid.leftNeighbours[0].edge2.middle);
                var vertex2 = vertexList.mypush(closestTrapezoid.leftNeighbours[1].edge2.middle);
                vertexes.push(vertex1, vertex2)

            }

            if (closestTrapezoid.rightNeighbours.length == 0) // last trapezoid
            {
                var vertex1 = vertexList.mypush(closestTrapezoid.center);
                vertexes.push(vertex1)
            }
            if (closestTrapezoid.rightNeighbours.length == 1) {
                var vertex1 = vertexList.mypush(closestTrapezoid.edge2.middle);
                vertexes.push(vertex1)
            } else if (closestTrapezoid.rightNeighbours.length == 2) {
                var vertex1 = vertexList.mypush(closestTrapezoid.rightNeighbours[0].edge1.middle);
                var vertex2 = vertexList.mypush(closestTrapezoid.rightNeighbours[1].edge1.middle);

                vertexes.push(vertex1, vertex2)
            }

            for (i = 0; i < vertexes.length; i++)
                vertexes[i].neighbours.push(newVertex);

            //var vertex = getClosest(event.point, vertexList); // to the closes vertex
            drawAstarShortestPath(0, newVertex.id)

        }
    }
}

function onMouseDrag(event) {
    if (segment)
        segment.point += event.delta;
    else if (path)
        path.position += event.delta;

    window.draw();
    drawDDD();
}

////////////////////////////////////  Objects /////////////////////////////////////

function PolygonPoint(p) {

    this.point = p;
    this.type = ''; // in, middle, out
    this.edgeExtension = ''; // up, down, both

    this.vector = null; // get vector to the region if available

    this.intPointUp = null, this.intPointDown = null; // intersection in the upper extension and intersection on the lower extension

    var up = new Path.Line(new Point(p.x, p.y - 5), new Point(p.x, 0));
    var down = new Path.Line(new Point(p.x, p.y + 5), new Point(p.x, canvas.height));

    var interUp = getObstacleCrossings(up);
    var interDown = getObstacleCrossings(down); // it has lower extension

    if (interUp.length % 2 == 1) // it has upper extension
    {
        this.intPointUp = getClosest(p, interUp);
        this.edgeExtension = 'up'
    }

    if (interDown.length % 2 == 1) {
        this.intPointDown = getClosest(p, interDown);
        this.edgeExtension = 'down'
    }

    if (this.intPointUp && this.intPointDown) // there are upper edge extension and lower edge extension
    {
        this.edgeExtension = 'both';
        var cir = new Path.Circle({center: this.point, radius: 5});
        cirInter = getObstacleCrossings(cir);

        var vec1 = cirInter[0].point - this.point; // this vector shows point to one edge
        var vec2 = cirInter[1].point - this.point; // this vector shows point to other edge

        this.vector = (vec1 + vec2);

        if (this.vector.x > 0)
            this.type = 'in';
        else if (this.vector.x < 0)
            this.type = 'out';
    }

    this.drawVerticalLine = function (p) {

        if (this.edgeExtension == 'up') // it has upper extension
            drawables.push(new Path.Line({from: p, to: this.intPointUp.point, strokeColor: 'red', strokeWidth: 4}));

        if (this.edgeExtension == 'down')
            drawables.push(new Path.Line({from: p, to: this.intPointDown.point, strokeColor: 'blue', strokeWidth: 4}));

        if (this.edgeExtension == 'both') {
            drawables.push(new Path.Line({from: p, to: this.intPointUp.point, strokeColor: 'red', strokeWidth: 4}));
            drawables.push(new Path.Line({from: p, to: this.intPointDown.point, strokeColor: 'blue', strokeWidth: 4}))
        }
    };

    if ($('.showEdges:checked').val()) //red and blue vertical lines
    {
        if ($('.hideBoustrophedonEdges:checked').val())
            if (this.type == 'in' || this.type == 'out') {
                this.drawVerticalLine(this.point)
            } else ;
        else
            this.drawVerticalLine(this.point)

    }
    if ($('.showPolygonPoints:checked').val()) // yellow points
    {
        drawables.push(new Path.Circle({center: p, radius: 5, fillColor: '#ffff00', strokeColor: '#000000'}));
        drawables.push(new PointText({
            point: this.point,
            fontSize: '16px',
            fillColor: 'black',
            content: ' ' + polygonPoints.length
        }));
    }
}

function Vertex(p) {

    if (typeof Vertex.counter == 'undefined')
        Vertex.counter = 0;

    this.id = Vertex.counter++; // newly created vertex will have different id
    this.point = p;

    this.neighbours = [];

    this.fscore = 9999;
    this.gscore = 9999;
    this.cameFrom = null;

    this.length = function (obj) {
        return (this.point - obj.point).length;
    };

    if ($('.showVertices:checked').val()) {
        drawables.push(new Path.Circle({
            name: 'vertice-' + this.id,
            center: p,
            radius: 7,
            fillColor: '#ffff00',
            strokeColor: '#000000'
        }));
        drawables.push(new PointText({
            name: 'text',
            point: p + [-4, 3],
            fontSize: '8px',
            fillColor: 'black',
            content: this.id
        }));
    }
}

function Edge(p1, p2, ref) {

    if (typeof Edge.counter == 'undefined')
        Edge.counter = 0;
    this.id = Edge.counter++;

    this.p1 = p1;
    this.p2 = p2;
    this.middle = (p1 + p2) / 2;
    this.refP = ref; // reference point related to polygon point

    this.getLength = function () {
        return (p1 - p2).length;
    };

}

function Trapezoid(edge1, edge2) {
    if (typeof Trapezoid.counter == 'undefined')
        Trapezoid.counter = 0;
    this.id = Trapezoid.counter++;

    this.edge1 = edge1;
    this.edge2 = edge2;

    var edge1Point = (edge1 instanceof Edge ? edge1.middle : edge1); // can be instanceof a point instead of Edge
    var edge2Point = (edge2 instanceof Edge ? edge2.middle : edge2);
    this.center = (edge1Point + edge2Point) / 2;

    this.leftNeighbours = [];
    this.rightNeighbours = [];

    if ($('.showTrapezoidals:checked').val()) {
        drawables.push(new Path.Circle({center: this.center, radius: 10, strokeColor: 'black', opacity: 0.4}));
        drawables.push(new PointText({
            point: this.center + [-5, 5],
            fontSize: '12px',
            fillColor: 'black',
            opacity: 0.4,
            content: this.id
        }));
    }
}

////////////////////////////////////     Visual Part     /////////////////////////////////////
function createPolygons(t) { // makes last element transparent

    var path = [];
    while (obstacles.length != 0)
        obstacles.pop().remove();

    var polygons2 = [
        [[425, 53], [172, 150], [319, 273], [260, 146]],
        [[292, 150], [457, 282], [219, 398], [373, 274]],
        [[150, 21], [22, 261], [193, 456], [516, 451], [676, 138], [529, 28]]
    ];

    var polygons1 = [
        [[200, 146], [111, 235], [258, 358], [363, 221]],
        [[294, 69], [591, 171], [436, 399], [473, 206]],
        [[150, 21], [22, 261], [193, 456], [516, 451], [676, 138], [529, 28]] // last element is enclosing region
    ];

    var polygons = [];
    if (t == 1)
        polygons = polygons1;
    else if (t == 2)
        polygons = polygons2;

    for (k = 0; k < polygons.length; k++) {
        path = new Path({fillColor: '#009dec', strokeColor: 'black', strokeWidth: 2});
        var pol = polygons[k];

        for (i = 0; i < pol.length; i++)
            path.add(pol[i]);

        path.name = 'obstacle';
        path.closed = true;
        obstacles.push(path);
    }
    path.name = 'outer';
    path.style = {fillColor: 'white', strokeWidth: 3}; // make last element
    path.sendToBack();
}

function clearDrawables() {
    for (i = 0; i < drawables.length; i++) // it is for lines, circles, texts etc.
        drawables[i].remove();

    for (i = 0; i < shortestPath.length; i++) // it is for shortest path only
        shortestPath[i].remove();

    polygonPoints.clear();
    trapezoidlist.clear();
    vertexList.clear();

    Trapezoid.counter = 0;
    Edge.counter = 0;
    Vertex.counter = 0
}

///////////////////////////////  Some Useful Functions   /////////////////////////////////////

Array.prototype.clear = function () {
    while (this.length) {
        this.pop();
    }
};

Array.prototype.mypush = function (p) { // if point is existing return its vertex, otherwise push and return new vertex
    for (var i = 0; i < this.length; i++)
        if (this[i].point == p)
            return this[i];

    var vertex = new Vertex(p);
    this.push(vertex);
    return vertex
};

function getClosest(center, intersections) {

    var dist, id = 0;
    var max = 999;

    for (var i = 0; i < intersections.length; i++) {
        if (intersections[i] instanceof PolygonPoint || intersections[i] instanceof Vertex)
            dist = center.getDistance(intersections[i].point);
        else if (intersections[i] instanceof Trapezoid)
            dist = center.getDistance(intersections[i].center);

        if (dist < max) {
            max = dist;
            id = i;
        }
    }
    return intersections[id];
}

function getObstacleCrossings(obj) {

    var inter = [];
    for (var i = 0; i < obstacles.length; i++)
        inter = inter.concat(obj.getCrossings(obstacles[i])); // predefined circled obstacles

    obj.remove();
    return inter;
}

/////////////////////////////   Functions which are related to method  //////////////////////
function getEdge(i, str) { // up, down or both

    var p = polygonPoints[i].point;
    var up = polygonPoints[i].intPointUp;
    var down = polygonPoints[i].intPointDown;

    if (str == 'up')
        return new Edge(up.point, p, i);

    if (str == 'down')
        return new Edge(p, down.point, i);

    // below code for str is 'both'
    if (up && !down)
        return new Edge(p, up.point, i);
    else if (!up && down)
        return new Edge(p, down.point, i);
    else if (up && down)
        return new Edge(up.point, down.point, i);
    else
        return p // return points itself
}

function getNextAvailablePaths(i) {

    var paths = []; // stores all available edges if it seperating, otherwise returns first available one
    var vector = polygonPoints[i].vector;

    for (j = i + 1; j < polygonPoints.length; j++) {
        var lineVec = polygonPoints[j].point - polygonPoints[i].point;
        var unitVec = lineVec / lineVec.length;

        var hit = null;
        for (k = 0; k < obstacles.length - 1; k++) { // except last obstacle, cause it is outer obstacle

            hit = obstacles[k].hitTest((polygonPoints[i].point + unitVec * 15), {
                stroke: true,
                fill: true,
                tolerance: 0
            });
            if (hit)
                break;
        }

        if (!(hit && hit.type == 'fill')) // starting from outside of the obstacle and passes through an obstacle
        {
            var inter = getObstacleCrossings(new Path.Line({from: polygonPoints[i].point, to: polygonPoints[j].point}));

            if (inter.length == 0)
                if (polygonPoints[i].type == 'in')
                    paths.push(j); // it is like 6->11,7   (they are actually determined in next for)
                else
                    return j // it can go like 4->6 in guide.png 
        }
    }

    var up = null, down = null; // this point is sperating, up and down must be filled
    for (k = 0; k < paths.length; k++) {
        var temp = polygonPoints[paths[k]].point;
        var p = temp - polygonPoints[i].point;

        if ((p.x * vector.y - p.y * vector.x) > 0) //  direction is left
        {
            if (!up) up = paths[k] // store only first found one
        } else // direction is right
        {
            if (!down) down = paths[k]
        }
    }
    if (!up || !down)
        return null;
    return [up, down];
}

// path transition i->j
function findAvailablePaths(i) {

    function obtainTrueStartingEdge(i, j) {

        var vec = polygonPoints[i].vector;

        if (polygonPoints[i].type == 'in') {
            var vec2 = polygonPoints[j].point - polygonPoints[i].point;
            if (vec.x * vec2.y - vec.y * vec2.x < 0)
                return getEdge(i, 'up');
            else
                return getEdge(i, 'down');
        } else
            return getEdge(i, 'both');
    }

    function obtainTrueEndingEdge(i, j) {

        var vec = polygonPoints[j].vector;

        if (polygonPoints[j].type == 'out') // find where previous point comes from
        {
            var vec2 = polygonPoints[i].point - polygonPoints[j].point;
            if (vec.x * vec2.y - vec.y * vec2.x < 0)
                return getEdge(j, 'down');
            else
                return getEdge(j, 'up');
        } else
            return getEdge(j, 'both');
    }

    var j = getNextAvailablePaths(i); // if next point is dead end, it returns null

    if (j == null)
        return;
    if (typeof (j) == 'number') // there is only one way
        //console.log(i+' -> '+j)
        createTrapezoid(i, j);
    else { // there are two ways such as 6-> 11,7
        //console.log(i+' -> '+j[0] +','+j[1]);
        createTrapezoid(i, j[0]);
        createTrapezoid(i, j[1])
    }

    function createTrapezoid(a, b) {
        var edge1 = obtainTrueStartingEdge(a, b);
        var edge2 = obtainTrueEndingEdge(a, b);

        trapezoidlist.push(new Trapezoid(edge1, edge2))
    }
}

window.draw = function () {

    clearDrawables();
    obtainAllPolygonPoints();

    for (i = 0; i < polygonPoints.length; i++)
        findAvailablePaths(i);

    trapezoidNeighbouring();
    vertexCreating();
};

function trapezoidNeighbouring() {

    for (i = 0; i < trapezoidlist.length; i++)
        for (j = i + 1; j < trapezoidlist.length; j++) {
            if (trapezoidlist[i].edge2.refP == trapezoidlist[j].edge1.refP) {
                trapezoidlist[i].rightNeighbours.push(trapezoidlist[j]);
                trapezoidlist[j].leftNeighbours.push(trapezoidlist[i])
            }
        }
}

/////////////////////////////// A * path search //////////////////////////

function vertexCreating() {

    for (var i = 0; i < trapezoidlist.length; i++) {

// This code segment for making edge middle as vertex positions        
        var leftVertex = null, rightVertex = null;
        var leftVertexes = [], rightVertexes = [];

        if (trapezoidlist[i].leftNeighbours.length == 0) // first trapezoid
            leftVertex = vertexList.mypush(trapezoidlist[i].center);
        else if (trapezoidlist[i].leftNeighbours.length == 1)
            leftVertex = vertexList.mypush(trapezoidlist[i].edge1.middle);
        else if (trapezoidlist[i].leftNeighbours.length == 2) {
            var vertex1 = vertexList.mypush(trapezoidlist[i].leftNeighbours[0].edge2.middle);
            var vertex2 = vertexList.mypush(trapezoidlist[i].leftNeighbours[1].edge2.middle);

            leftVertexes.push(vertex1, vertex2)
        }

        if (trapezoidlist[i].rightNeighbours.length == 0) // last trapezoid
            rightVertex = vertexList.mypush(trapezoidlist[i].center);
        if (trapezoidlist[i].rightNeighbours.length == 1)
            rightVertex = vertexList.mypush(trapezoidlist[i].edge2.middle);
        else if (trapezoidlist[i].rightNeighbours.length == 2) {
            var vertex1 = vertexList.mypush(trapezoidlist[i].rightNeighbours[0].edge1.middle);
            var vertex2 = vertexList.mypush(trapezoidlist[i].rightNeighbours[1].edge1.middle);

            rightVertexes.push(vertex1, vertex2)
        }


// This code segment for making cell center as vertex positions        
//        var leftVertex = null, rightVertex = null;
//        var leftVertexes = [], rightVertexes = [];
//        
//        if(trapezoidlist[i].leftNeighbours.length == 0) // first trapezoid
//            leftVertex = vertexList.mypush(trapezoidlist[0].center);
//        else if(trapezoidlist[i].leftNeighbours.length == 1)
//        {
//            leftVertex = vertexList.mypush(trapezoidlist[i].leftNeighbours[0].center);
//        }
//        else if(trapezoidlist[i].leftNeighbours.length == 2)
//        {
//            var vertex1 = vertexList.mypush(trapezoidlist[i].leftNeighbours[0].center);
//            var vertex2 = vertexList.mypush(trapezoidlist[i].leftNeighbours[1].center);
//            leftVertexes.push(vertex1, vertex2)
//        }
//        
//        if(trapezoidlist[i].leftNeighbours.length >= 1)
//            rightVertex = vertexList.mypush(trapezoidlist[i].center);


        //////////////////// neighbouring part ////////////////////

        if (leftVertex && rightVertex)
            makeVertexNeighbour(leftVertex, rightVertex);
        else if (leftVertex && rightVertexes.length == 2) {
            makeVertexNeighbour(leftVertex, rightVertexes[0]);
            makeVertexNeighbour(leftVertex, rightVertexes[1]);
        } else if (leftVertexes.length == 2 && rightVertex) {
            makeVertexNeighbour(leftVertexes[0], rightVertex);
            makeVertexNeighbour(leftVertexes[1], rightVertex);
        } else if (leftVertexes.length == 2 && rightVertexes.length == 2) {
            makeVertexNeighbour(leftVertexes[0], rightVertexes[0]);
            makeVertexNeighbour(leftVertexes[0], rightVertexes[1]);
            makeVertexNeighbour(leftVertexes[1], rightVertexes[0]);
            makeVertexNeighbour(leftVertexes[1], rightVertexes[1]);
        }
    }

    function makeVertexNeighbour(left, right) {
        left.neighbours.push(right);
        right.neighbours.push(left);
    }

}

function heuristic(vstart, vgoal) {
    return (vstart.point - vgoal.point).length;
}

shortestPath = [];

function Astar(start, goal) {
    var closedSet = [];
    var openSet = [vertexList[start]];

    vertexList[start].gscore = 0;
    vertexList[start].fscore = heuristic(vertexList[start], vertexList[goal]);

    while (openSet.length > 0) {
        var current = mindist(openSet);
        if (current == vertexList[goal]) {
            console.log('bongoooooo');
            return
        }

        openSet = openSet.filter(function (el) {
            return el.id != current.id;
        }); // remove object with that id
        closedSet.push(current);

        for (i = 0; i < current.neighbours.length; i++) {
            var neighbour = current.neighbours[i];
            if (IndexOf(neighbour, closedSet))
                continue;

            var tentative_g_score = current.gscore + current.length(neighbour);

            if (!IndexOf(neighbour, openSet))
                openSet.push(neighbour);
            else if (tentative_g_score >= neighbour.gscore)
                continue;

            neighbour.cameFrom = current;
            neighbour.gscore = tentative_g_score;
            neighbour.fscore = neighbour.gscore + heuristic(neighbour, vertexList[goal]);
        }
    }

    function IndexOf(item, myArray) {
        for (var i = 0, len = myArray.length; i < len; i++) {
            if (myArray[i].id == item.id) return true;
        }
        return false;
    }

    function mindist(openSet) {
        var result = null;
        var fscore = 9999;
        for (i = 0; i < openSet.length; i++) {
            var temp = openSet[i].fscore;
            if (temp < fscore) {
                fscore = temp;
                result = openSet[i];
            }
        }
        return result;
    }
}

function drawAstarShortestPath(startId, goalId) {
    Astar(startId, goalId);

    for (i = 0; i < shortestPath.length; i++)
        shortestPath[i].remove();

    node = vertexList[goalId];
    while (node.cameFrom != null && node.id != startId) {
        var ln = new Path.Line({from: node.point, to: node.cameFrom.point, strokeColor: '#00ff00', strokeWidth: 5});
        shortestPath.push(ln);
        node = node.cameFrom;
    }
}

///////////////////////////////    Main    ///////////////////////////////

//createPolygons(1);
//window.draw();

window.loadMap1 = function () {
    createPolygons(1);
    window.draw()
};
window.loadMap2 = function () {
    createPolygons(2);
    window.draw()
};

window.reset = function () {

};

function onFrame() {

}


////////// Some Miscalleneous Functions ///////
window.getPolypolygonPoints = function () {
    for (i = 0; i < obstacles.length; i++) {
        var seg = obstacles[i].segments;

        var str = "";
        for (k = 0; k < seg.length; k++)
            str += "[" + seg[k].point.x + "," + seg[k].point.y + "],";
        console.info(str)
    }
};

function obtainAllPolygonPoints() {

    var tempPoints = [];

    for (i = 0; i < obstacles.length; i++) { // obtain polygonPoints from obstacles
        var seg = obstacles[i].segments;

        for (k = 0; k < seg.length; k++)
            tempPoints.push(new Point(seg[k].point.x, seg[k].point.y))
    }

    tempPoints.sort(function (a, b) {  // sort according to x value
        return a.x > b.x ? 1 : -1;
    });

    for (i = 0; i < tempPoints.length; i++)
        polygonPoints.push(new PolygonPoint(tempPoints[i]))
} // returns all points in order according to their x value


function drawDDD() {

    var scope = new paper.PaperScope();
    var canvas_2 = document.getElementById('myCanvas2');
    scope.setup(canvas_2);
    canvas_2.width = 360;
    canvas_2.height = 240;


    for (i = 0; i < trapezoidlist.length; i++) {
        for (j = 0; j < trapezoidlist[i].rightNeighbours.length; j++) {
            var ne = trapezoidlist[i].rightNeighbours[j];
            drawables.push(new Path.Line({
                from: trapezoidlist[i].center / 2,
                to: ne.center / 2,
                strokeColor: 'black',
                strokeWidth: 2
            }));
        }

        drawables.push(new Path.Circle({
            center: trapezoidlist[i].center / 2,
            radius: 10,
            fillColor: 'white',
            strokeColor: 'black',
            strokeWidth: 1
        }));
        drawables.push(new PointText({
            point: trapezoidlist[i].center / 2 + [-5, 5],
            fontSize: '12px',
            fillColor: 'black',
            content: trapezoidlist[i].id
        }));
    }
    scope.view.draw();
}

drawDDD();