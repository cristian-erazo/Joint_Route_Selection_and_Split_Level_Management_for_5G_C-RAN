graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 10
    prb 6
    x 104
    y 90
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 4
    prb 3
    x 57
    y 78
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 4
    prb 5
    x 103
    y 100
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 6
    prb 4
    x 98
    y 86
  ]
  edge [
    source 0
    target 3
    bandwith 275
    delay 151
  ]
  edge [
    source 0
    target 2
    bandwith 813
    delay 400
  ]
  edge [
    source 1
    target 5
    bandwith 104
    delay 411
  ]
  edge [
    source 1
    target 4
    bandwith 529
    delay 377
  ]
]
