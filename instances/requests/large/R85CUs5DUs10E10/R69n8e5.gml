graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 5
    prb 5
    x 74
    y 87
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 5
    prb 2
    x 120
    y 97
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 7
    prb 6
    x 46
    y 19
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 9
    prb 6
    x 102
    y 107
  ]
  node [
    id 7
    label "7"
    type 1
    prc 5
    ant 10
    prb 3
    x 112
    y 21
  ]
  edge [
    source 0
    target 6
    bandwith 280
    delay 355
  ]
  edge [
    source 1
    target 7
    bandwith 200
    delay 170
  ]
  edge [
    source 1
    target 4
    bandwith 627
    delay 293
  ]
  edge [
    source 2
    target 5
    bandwith 697
    delay 226
  ]
  edge [
    source 2
    target 3
    bandwith 400
    delay 307
  ]
]
