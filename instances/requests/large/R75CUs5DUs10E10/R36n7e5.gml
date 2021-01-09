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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 4
    prb 4
    x 54
    y 82
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 4
    prb 2
    x 70
    y 106
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 5
    prb 6
    x 30
    y 26
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 5
    prb 5
    x 16
    y 83
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 9
    prb 4
    x 74
    y 37
  ]
  edge [
    source 0
    target 2
    bandwith 896
    delay 311
  ]
  edge [
    source 0
    target 3
    bandwith 279
    delay 422
  ]
  edge [
    source 1
    target 6
    bandwith 618
    delay 440
  ]
  edge [
    source 1
    target 5
    bandwith 376
    delay 498
  ]
  edge [
    source 1
    target 4
    bandwith 317
    delay 352
  ]
]
