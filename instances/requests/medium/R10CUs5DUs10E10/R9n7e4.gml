graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 9
    prb 3
    x 12
    y 68
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 7
    prb 6
    x 15
    y 70
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 10
    prb 3
    x 63
    y 29
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 4
    prb 4
    x 69
    y 68
  ]
  edge [
    source 0
    target 3
    bandwith 431
    delay 145
  ]
  edge [
    source 1
    target 5
    bandwith 319
    delay 302
  ]
  edge [
    source 2
    target 6
    bandwith 961
    delay 471
  ]
  edge [
    source 2
    target 4
    bandwith 680
    delay 237
  ]
]
